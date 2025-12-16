const { createApp, ref, onMounted } = Vue;

const app = createApp({
    setup() {
        const currentView = ref('stores');

        // --- Auth State ---
        const isAuthenticated = ref(false);
        const isRegistering = ref(false);
        const authForm = ref({ username: '', password: '' });
        const authError = ref('');

        const startApp = async () => {
            // Check if we have tokens, simplified check
            if (localStorage.getItem('accessToken')) {
                isAuthenticated.value = true;
                await fetchStores();
                fetchSystemStatus();
            }
        };

        const toggleAuthMode = () => {
            isRegistering.value = !isRegistering.value;
            authError.value = '';
        };

        const authSubmit = async () => {
            authError.value = '';
            const endpoint = isRegistering.value ? '/api/auth/register' : '/api/auth/login';
            try {
                const res = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(authForm.value)
                });

                if (isRegistering.value) {
                    if (res.ok) {
                        alert("Регистрация успешна! Теперь войдите.");
                        isRegistering.value = false;
                    } else {
                        const msg = await res.text();
                        authError.value = msg || "Ошибка регистрации";
                    }
                } else {
                    if (res.ok) {
                        const tokens = await res.json();
                        localStorage.setItem('accessToken', tokens.accessToken);
                        localStorage.setItem('refreshToken', tokens.refreshToken);
                        isAuthenticated.value = true;
                        authForm.value = { username: '', password: '' };
                        fetchStores();
                        fetchSystemStatus();
                    } else {
                        authError.value = "Неверные учетные данные";
                    }
                }
            } catch (e) {
                console.error(e);
                authError.value = "Ошибка сети";
            }
        };

        const logout = () => {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            isAuthenticated.value = false;
            stores.value = [];
            currentView.value = 'stores';
        };

        // --- HTTP Client with Interceptor logic ---
        const apiFetch = async (url, options = {}) => {
            let token = localStorage.getItem('accessToken');
            const headers = { ...options.headers };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const response = await fetch(url, { ...options, headers });

            if (response.status === 403 || response.status === 401) {
                // Try refresh
                const refreshRes = await fetch('/api/auth/refresh', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ refreshToken: localStorage.getItem('refreshToken') })
                });

                if (refreshRes.ok) {
                    const data = await refreshRes.json();
                    localStorage.setItem('accessToken', data.accessToken);
                    // Retry original request
                    headers['Authorization'] = `Bearer ${data.accessToken}`;
                    return fetch(url, { ...options, headers });
                } else {
                    // Refresh failed
                    logout();
                    throw new Error("Session expired");
                }
            }
            return response;
        };


        // --- App Data ---
        const stores = ref([]);
        const mps = ref([]);
        const selectedStore = ref(null);
        const selectedMp = ref(null);

        // Modals
        const showCreateStoreModal = ref(false);
        const newStoreName = ref('');
        const showCreateMpModal = ref(false);
        const newMpName = ref('');
        const fileInput = ref(null);
        const uploading = ref(false);

        // Search & System
        const searchQuery = ref('');
        const searchResults = ref([]);
        const systemStatus = ref(null);

        const API_URL = '/api';

        // --- Store Actions ---
        const fetchStores = async () => {
            if (!isAuthenticated.value) return;
            try {
                const res = await apiFetch(`${API_URL}/stores`);
                stores.value = await res.json();
            } catch (e) { console.error(e); }
        };

        const createStore = async () => {
            if (!newStoreName.value.trim()) return;
            try {
                const res = await apiFetch(`${API_URL}/stores`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newStoreName.value })
                });
                if (res.ok) {
                    await fetchStores();
                    showCreateStoreModal.value = false;
                    newStoreName.value = '';
                }
            } catch (e) { console.error(e); }
        };

        const deleteStore = async (id) => {
            if (!confirm("Вы уверены?")) return;
            await apiFetch(`${API_URL}/stores/${id}`, { method: 'DELETE' });
            await fetchStores();
        };

        const openStore = async (store) => {
            selectedStore.value = store;
            currentView.value = 'store-details';
            await fetchMps(store.id);
        };

        // --- MP Actions ---
        const fetchMps = async (storeId) => {
            const res = await apiFetch(`${API_URL}/stores/${storeId}/mp`);
            mps.value = await res.json();
        };

        const createMp = async () => {
            if (!newMpName.value.trim()) return;
            try {
                const res = await apiFetch(`${API_URL}/stores/${selectedStore.value.id}/mp`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newMpName.value })
                });
                if (res.ok) {
                    await fetchMps(selectedStore.value.id);
                    showCreateMpModal.value = false;
                    newMpName.value = '';
                }
            } catch (e) { console.error(e); }
        };

        const deleteMp = async (id) => {
            if (!confirm("Вы уверены?")) return;
            await apiFetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${id}`, { method: 'DELETE' });
            await fetchMps(selectedStore.value.id);
        };

        const openMp = async (mp) => {
            const res = await apiFetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${mp.id}`);
            selectedMp.value = await res.json();
            currentView.value = 'mp-details';
        };

        // --- Packing List Actions ---
        const uploadPackingList = async () => {
            if (!fileInput.value.files[0]) return;
            uploading.value = true;
            const formData = new FormData();
            formData.append('file', fileInput.value.files[0]);

            try {
                const res = await apiFetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${selectedMp.value.id}/packing/upload`, {
                    method: 'POST',
                    body: formData
                });
                if (res.ok) {
                    await openMp(selectedMp.value);
                    fileInput.value.value = '';
                    alert("Загрузка успешна!");
                } else {
                    alert("Ошибка загрузки.");
                }
            } catch (e) {
                console.error(e);
                alert("Ошибка отправки файла.");
            } finally {
                uploading.value = false;
            }
        };

        const deleteItem = async (itemId) => {
            if (!confirm("Удалить позицию?")) return;
            await apiFetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${selectedMp.value.id}/packing/${itemId}`, { method: 'DELETE' });
            await openMp(selectedMp.value);
        };


        // Actions
        const fetchSystemStatus = async () => {
            try {
                const res = await apiFetch(`${API_URL}/system/status`);
                systemStatus.value = await res.json();
            } catch (e) { console.error("Failed to fetch system status", e); }
        };

        const search = async () => {
            if (!searchQuery.value.trim()) return;
            try {
                const res = await apiFetch(`${API_URL}/search?q=${encodeURIComponent(searchQuery.value)}`);
                searchResults.value = await res.json();
                currentView.value = 'search-results';
            } catch (e) { console.error(e); }
        };

        // Init
        onMounted(() => {
            startApp();
        });

        return {
            currentView,
            // Auth
            isAuthenticated,
            isRegistering,
            authForm,
            authError,
            authSubmit,
            toggleAuthMode,
            logout,
            // Data
            stores,
            mps,
            selectedStore,
            selectedMp,
            // Search
            searchQuery,
            searchResults,
            search,
            // System
            systemStatus,
            // Modals
            showCreateStoreModal,
            newStoreName,
            showCreateMpModal,
            newMpName,
            fileInput,
            uploading,
            // Actions
            createStore,
            deleteStore,
            openStore,
            createMp,
            deleteMp,
            openMp,
            uploadPackingList,
            deleteItem
        };
    }
});

app.mount('#app');

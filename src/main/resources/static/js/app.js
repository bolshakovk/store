const { createApp, ref, onMounted } = Vue;

const app = createApp({
    setup() {
        const currentView = ref('stores');
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

        // API Base
        const API_URL = '/api';

        // --- Store Actions ---
        const fetchStores = async () => {
            try {
                const res = await fetch(`${API_URL}/stores`);
                stores.value = await res.json();
            } catch (e) {
                console.error("Error fetching stores:", e);
                alert("Failed to fetch stores.");
            }
        };

        const createStore = async () => {
            if (!newStoreName.value.trim()) return;
            try {
                const res = await fetch(`${API_URL}/stores`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newStoreName.value })
                });
                if (res.ok) {
                    await fetchStores();
                    showCreateStoreModal.value = false;
                    newStoreName.value = '';
                }
            } catch (e) {
                console.error(e);
            }
        };

        const deleteStore = async (id) => {
            if (!confirm("Are you sure?")) return;
            await fetch(`${API_URL}/stores/${id}`, { method: 'DELETE' });
            await fetchStores();
        };

        const openStore = async (store) => {
            selectedStore.value = store;
            currentView.value = 'store-details';
            await fetchMps(store.id);
        };

        // --- MP Actions ---
        const fetchMps = async (storeId) => {
            const res = await fetch(`${API_URL}/stores/${storeId}/mp`);
            mps.value = await res.json();
        };

        const createMp = async () => {
            if (!newMpName.value.trim()) return;
            try {
                const res = await fetch(`${API_URL}/stores/${selectedStore.value.id}/mp`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newMpName.value })
                });
                if (res.ok) {
                    await fetchMps(selectedStore.value.id);
                    showCreateMpModal.value = false;
                    newMpName.value = '';
                }
            } catch (e) {
                console.error(e);
            }
        };

        const deleteMp = async (id) => {
            if (!confirm("Are you sure?")) return;
            await fetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${id}`, { method: 'DELETE' });
            await fetchMps(selectedStore.value.id);
        };

        const openMp = async (mp) => {
            // Fetch fresh MP details including packing list
            const res = await fetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${mp.id}`);
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
                const res = await fetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${selectedMp.value.id}/packing/upload`, {
                    method: 'POST',
                    body: formData
                });
                if (res.ok) {
                    // Refresh MP details
                    await openMp(selectedMp.value);
                    fileInput.value.value = ''; // clear input
                    alert("Upload successful!");
                } else {
                    alert("Upload failed.");
                }
            } catch (e) {
                console.error(e);
                alert("Error submitting file.");
            } finally {
                uploading.value = false;
            }
        };

        const deleteItem = async (itemId) => {
            if (!confirm("Delete this item?")) return;
            await fetch(`${API_URL}/stores/${selectedStore.value.id}/mp/${selectedMp.value.id}/packing/${itemId}`, { method: 'DELETE' });
            // Refresh
            await openMp(selectedMp.value);
        };

        const searchQuery = ref('');
        const searchResults = ref([]);

        // Actions
        const search = async () => {
            if (!searchQuery.value.trim()) return;
            try {
                const res = await fetch(`${API_URL}/search?q=${encodeURIComponent(searchQuery.value)}`);
                searchResults.value = await res.json();
                currentView.value = 'search-results';
            } catch (e) {
                console.error(e);
            }
        };

        // Init
        onMounted(() => {
            fetchStores();
        });

        return {
            currentView,
            stores,
            mps,
            selectedStore,
            selectedMp,
            // Search
            searchQuery,
            searchResults,
            search,
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

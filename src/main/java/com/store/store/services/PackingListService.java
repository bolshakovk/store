package com.store.store.services;
import com.store.store.models.enitities.PackingList;
import com.store.store.repository.PackingListItemRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PackingListService {

    private final PackingListItemRepository repository;
    private final MpService mpService;

    public PackingListService(PackingListItemRepository repository, MpService mpService) {
        this.repository = repository;
        this.mpService = mpService;
    }
    public List<PackingList> findByMp(Long mpId) {
        return repository.findByMpId(mpId);
    }
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }
    public List<PackingList> search(String text) {
        return repository.searchByMarkOrDrawing(text);
    }

    @Transactional
    public void uploadExcel(Long mpId, MultipartFile file) throws Exception {
        var mp = mpService.getById(mpId);

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(1);
            // поиск индексов колонок
            Map<String, Integer> columns = findColumns(sheet);

            int startRow = 12; // данные начинаются сразу после заголовков (строка 12, индекс 12)
            System.out.println(findRowByText(sheet, 1, "Итого:"));
            for (int i = startRow; i <= findRowByText(sheet, 1, "Итого:"); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String drawingNumber = getMergedCellValue(sheet, i, columns.get("drawing")).trim();
                // если встретили ИТОГО — заканчиваем цикл
                if ("ИТОГО".equalsIgnoreCase(drawingNumber)) break;

                PackingList item = new PackingList();
                item.setMp(mp);
                System.out.println(drawingNumber);
                item.setDrawingNumber(drawingNumber);
                System.out.println(getMergedCellValue(sheet, i, columns.get("mark")));
                item.setMarkNumber(getMergedCellValue(sheet, i, columns.get("mark")));
                System.out.println(getMergedCellValue(sheet, i, columns.get("name")));
                item.setName(getMergedCellValue(sheet, i, columns.get("name")));
                item.setQuantity(getInt(row, columns.get("quantity")));
                System.out.println(getInt(row, columns.get("quantity")));
                System.out.println(getMergedCellValue(sheet, i, columns.get("montage")));
                item.setMpCode(getMergedCellValue(sheet, i, columns.get("montage")));
                item.setWeight((double) 0);

                repository.save(item);
            }
        }
    }

    public int findRowByText(Sheet sheet, int colIndex, String text) {
        for (int r = 0; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String cellValue = getMergedCellValue(sheet, r, colIndex).trim();
            if (cellValue.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return -1; // не найдено
    }

    private String getMergedCellValue(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return "";
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.isInRange(rowIndex, colIndex)) {
                Cell firstCell = sheet.getRow(range.getFirstRow()).getCell(range.getFirstColumn());
                return cellToString(firstCell);
            }
        }
        return cellToString(cell);
    }

    private String cellToString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private Map<String, Integer> findColumns(Sheet sheet) {
        Map<String, Integer> map = new HashMap<>();
        // предположим, что заголовки находятся на строке 11 (индекс 10)
        Row headerRow = sheet.getRow(11);
        if (headerRow == null) throw new IllegalStateException("Строка заголовков не найдена");

        for (Cell cell : headerRow) {
            String text = getMergedCellValue(sheet, 11, cell.getColumnIndex()).trim();
            switch (text) {
                case "№ чертежа" -> map.put("drawing", cell.getColumnIndex());
                case "№ марки" -> map.put("mark", cell.getColumnIndex());
                case "Наименование" -> map.put("name", cell.getColumnIndex());
                case "Кол-во, шт." -> map.put("quantity", cell.getColumnIndex());
                case "Монтажная партия" -> map.put("montage", cell.getColumnIndex());
                case "Вес, кг" -> map.put("weight", cell.getColumnIndex());
            }
        }

        if (map.size() != 5)
            throw new IllegalStateException("Excel не содержит всех необходимых колонок");

        return map;
    }


    private String getString(Row row, Integer index) {
        if (index == null) return null;
        var cell = row.getCell(index);
        return cell == null ? null : cell.toString().trim();
    }

    private Integer getInt(Row row, Integer index) {
        var cell = row.getCell(index);
        if (cell == null) return 0;
        return (int) cell.getNumericCellValue();
    }
}
package com.cafe.serviceImpl;

import com.cafe.JWT.JwtFilter;
import com.cafe.constents.CafeConstants;
import com.cafe.dao.BillDao;
import com.cafe.model.Bill;
import com.cafe.service.BillService;
import com.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");
        String fileName = "";
        try {
            if (validateRequestMap(requestMap)){
                 if (requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                     fileName= (String)requestMap.get("uuid");
                 } else{
                     fileName = CafeUtils.getUUID();
                     requestMap.put("uuid",fileName);
                     insertBill(requestMap);
                 }
                String data = "Name : " + requestMap.get("name") + "\n" +
                        "Contact Number : " + requestMap.get("contactNumber") + "\n" +
                        "Email : " + requestMap.get("email") + "\n" +
                        "Payment Method : " + requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document,
                        new FileOutputStream(CafeConstants.STORE_LOCATION + "\\"+ fileName + ".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System", getFront("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data + "\n\n", getFront("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));

                for(int i = 0; i < jsonArray.length(); i++){
                    addRows(table,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);
                Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") +
                        "\n" + "Thank you for the visiting.Please visit again!! " ,getFront("Data"));
                document.add(footer);
                document.close();

                return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Required data not found", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }
    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal((Integer) requestMap.get("totalAmount"));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            billDao.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside the setRectangleInPdf");
        Rectangle rectangle = new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);

        document.add(rectangle);
    }

    private Font getFront(String type) {
        log.info("Inside the getFont");
        switch (type){
            case "Header":
                Font header = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                header.setStyle(Font.BOLD);
                return header;
            case "Data":
                Font data = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                data.setStyle(Font.BOLD);
                return data;
            default:
                return new Font();
        }
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside the addTableHeader");
        Stream.of("Name","category","Quantity","Price","Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, Map<String, String> data) {
        log.info("Inside the addRows");
        table.addCell(data.get("name"));
        table.addCell(data.get("category"));
        table.addCell(data.get("quantity"));
        table.addCell(String.valueOf(data.get("price")));
        table.addCell(String.valueOf(data.get("total")));
    }
}

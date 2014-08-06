package com.eastbay.performancezone.print.Service;

import com.eastbay.performancezone.print.Request.PrintRequest;
import com.eastbay.performancezone.print.Request.Product;
import com.eastbay.performancezone.print.Response.Message;
import com.eastbay.performancezone.print.Response.PrintResponse;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/18/14
 * Time: 4:21 PM
 * SDG
 */
public class PrintServiceTest {
    private static Logger logger = Logger.getLogger(PrintServiceTest.class);

    @Test
    public void testPrintProductsErrors() throws Exception {
        PrintService printService = new PrintService();
        String jsonInput;
        String jsonOutput;
        PrintResponse response;
        String msgToFind;
        boolean found;
        jsonInput = null;
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        found = false;
        msgToFind = PrintResponse.ERROR_EMPTY_REQUEST;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        jsonInput = "";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY, response.getError());
        jsonInput = "xxx";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY, response.getError());
        jsonInput = "{}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        found = false;
        msgToFind = PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        jsonInput = "{\"x\":\"y\"}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        found = false;
        msgToFind = PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        jsonInput = "{\"products\":[]}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        found = false;
        msgToFind = PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        jsonInput = "{\"testSuccess\":true,\"testFail\":true,\"products\":[]}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        jsonInput = "{\"testSuccess\":true,\"products\":[]}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertNull(response.getError());
        jsonInput = "{\"products\":[{}]}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        found = false;
        msgToFind = PrintResponse.ERROR_PRODUCT_NAME_CAN_NOT_BE_BLANK;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
    }

    @Test
    public void testPrintProductsNoErrors() throws Exception {
        PrintService printService = new PrintService();
        String jsonInput;
        String jsonOutput;
        PrintResponse response;
        String msgToFind;
        boolean found;
        jsonInput = "{\"products\":[{\"brand\":\"Nike\",\"number\":\"31425019\",\"name\":\"Vapor Carbon Elite 2014 TD\",\"price\":149.99,\"extendedPrice\":149.99,\"quantity\":1}]}";
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertNull(response.getError());
        found = false;
        msgToFind = PrintResponse.WARN_COLOR_IS_BLANK;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        PrintRequest request = PrintRequest.fromJsonToPrintRequest(jsonInput);
        request.setTestFail(false);
        request.setTestSuccess(false);
        Product product = request.getProducts()[0];
        product.setBarCode("12345");
        product.setColor("Black/White/Black");
        product.setDescription("Athletic Shoes");
        product.setImageURL("http://images.eastbay.com/is/image/EBFL/1425011?wid=2000&hei=2000&qlt=60");
        product.setMicroSite(true);
        product.setSize("10.5");
        System.out.println(request.toJson());
        jsonInput = request.toJson();
        jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        response = PrintResponse.fromJsonToPrintResponse(jsonOutput);
        Assert.assertNull(response.getError());
        Assert.assertNull(response.getMessages());
    }

    @Test
    public void testValidateRequestNullRequest() throws Exception {
        PrintService printService = new PrintService();
        PrintRequest request = null;
        PrintResponse response;
        response = new PrintResponse();
        printService.validateRequest(request, response);
        Assert.assertNotNull(response.getError());
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        Assert.assertTrue(response.getMessages().length > 0);
        boolean found = false;
        String msgToFind = PrintResponse.ERROR_EMPTY_REQUEST;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
    }

    @Test
    public void testValidateRequestBlankRequest() throws Exception {
        PrintService printService = new PrintService();
        PrintRequest request = new PrintRequest();
        PrintResponse response;
        response = new PrintResponse();
        printService.validateRequest(request, response);
        Assert.assertNotNull(response.getError());
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        Assert.assertTrue(response.getMessages().length > 0);
        boolean found = false;
        String msgToFind = PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
    }

    @Test
    public void testValidateRequestBlankProduct() throws Exception {
        PrintService printService = new PrintService();
        PrintRequest request = new PrintRequest();
        Product[] products = new Product[1];
        products[0] = new Product();
        request.setProducts(products);
        PrintResponse response;
        response = new PrintResponse();
        printService.validateRequest(request, response);
        Assert.assertNotNull(response.getError());
        Assert.assertEquals(PrintResponse.ERROR_VALIDATIONS, response.getError());
        Assert.assertTrue(response.getMessages().length > 0);
        boolean found = false;
        String msgToFind = PrintResponse.ERROR_PRICE_IS_INVALID;
        for (Message message : response.getMessages()) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
    }

    @Test
    public void testValidProductBlankProduct() throws Exception {
        PrintService printService = new PrintService();
        Product product = new Product();
        ArrayList<Message> messages = new ArrayList<>();
        boolean valid = printService.validProduct(product, messages);
        Assert.assertFalse(valid);
        boolean found = false;
        String msgToFind = PrintResponse.ERROR_EXTENDED_PRICE_IS_INVALID;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.ERROR_PRICE_IS_INVALID;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.ERROR_QUANTITY_IS_INVALID;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.ERROR_PRODUCT_NAME_CAN_NOT_BE_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.WARN_BAR_CODE_IS_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.WARN_COLOR_IS_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.WARN_DESCRIPTION_IS_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.WARN_IMAGE_URL_IS_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.WARN_SIZE_IS_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.ERROR_PRODUCT_BRAND_CAN_NOT_BE_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
        found = false;
        msgToFind = PrintResponse.ERROR_PRODUCT_NUMBER_CAN_NOT_BE_BLANK;
        for (Message message : messages) {
            if (msgToFind.equals(message.getMessage())) {
                found = true;
            }
        }
        Assert.assertTrue("Should have found a message with '" + msgToFind + "'", found);
    }

    @Test
    public void testPrintPageHtml() throws Exception {
        FileInputStream htmlStream = null;
        try {
            URL url = getClass().getResource("/files/pick-list.html");

            File file = new File(url.toURI());
            htmlStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        if (htmlStream == null) {
            Assert.fail();
        }
        //DocFlavor htmlInFormat = DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16;
        DocFlavor htmlInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc myDoc = new SimpleDoc(htmlStream, htmlInFormat, null);
        PrintRequestAttributeSet aSet = new HashPrintRequestAttributeSet();
        aSet.add(new Copies(1));
        //aSet.add(MediaSize.NA.LETTER);
        aSet.add(Sides.ONE_SIDED);
        javax.print.PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        if (service != null) {
            DocPrintJob job = service.createPrintJob();
            try {
                job.print(myDoc, aSet);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testPrintPagePdf() throws Exception {
        FileInputStream htmlStream = null;
        try {
            URL url = getClass().getResource("/files/orderticket-image-051414b.pdf");

            File file = new File(url.toURI());
            htmlStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        if (htmlStream == null) {
            Assert.fail();
        }
        //DocFlavor htmlInFormat = DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16;
        DocFlavor htmlInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc myDoc = new SimpleDoc(htmlStream, htmlInFormat, null);
        PrintRequestAttributeSet aSet = new HashPrintRequestAttributeSet();
        aSet.add(new Copies(1));
        //aSet.add(MediaSize.NA.LETTER);
        aSet.add(Sides.ONE_SIDED);
        javax.print.PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        if (service != null) {
            DocPrintJob job = service.createPrintJob();
            try {
                job.print(myDoc, aSet);
            } catch (PrintException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testListAllPrintServices() {
        javax.print.PrintService[] allServices =
                PrintServiceLookup.lookupPrintServices(null, null);
        for (javax.print.PrintService ps : allServices) {
            logger.debug(ps + " supports :");
            DocFlavor[] flavors = ps.getSupportedDocFlavors();
            for (DocFlavor flavor : flavors) {
                logger.debug("\t" + flavor);
            }
        }
    }

    @Test
    public void testPrintProductsJerryKort() {
        PrintService printService = new PrintService();
        String jsonInput = "{\"products\": [\n" +
                "    {\n" +
                "        \"barCode\": \"54321\",\n" +
                "        \"brand\": \"Nike\",\n" +
                "        \"color\": \"Black/White/Black\",\n" +
                "        \"description\": \"Athletic Shoes\",\n" +
                "        \"extendedPrice\": 149.99,\n" +
                "        \"imageURL\": \"http://images.eastbay.com/is/image/EBFL/1425011?wid=2000&hei=2000&qlt=60&fmt=png-alpha\",\n" +
                "        \"microSite\": true,\n" +
                "        \"name\": \"Vapor Carbon Elite 2014 TD\",\n" +
                "        \"number\": \"31425019\",\n" +
                "        \"price\": 149.99,\n" +
                "        \"quantity\": 1,\n" +
                "        \"size\": \"10.5\"\n" +
                "}]}";
        String jsonOutput = printService.printProducts(jsonInput);
        Assert.assertNotNull(jsonOutput);
        System.out.println(jsonOutput);
    }
}

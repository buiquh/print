package com.eastbay.performancezone.print.Service;

import com.eastbay.performancezone.print.Request.PrintRequest;
import com.eastbay.performancezone.print.Request.Product;
import com.eastbay.performancezone.print.Response.Message;
import com.eastbay.performancezone.print.Response.PrintResponse;
import com.eastbay.performancezone.print.content.PickList;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/18/14
 * Time: 3:29 PM
 * SDG
 */
public class PrintService {

    private static Logger logger = Logger.getLogger(PrintService.class);

    public String printProducts(String jsonInput) {
        String jsonOutput;
        PrintResponse response = new PrintResponse();
        PrintRequest request;
        try {
            request = PrintRequest.fromJsonToPrintRequest(jsonInput);
            validateRequest(request, response);
            if (response.getError() == null)
                printRequest(request, response);
        } catch (flexjson.JSONException e) {
            response.setError(PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY);
        } catch (Exception e) {
            logger.error("error handling print requests", e);
            response.setError(PrintResponse.ERROR_UNKNOWN_ERROR);
        }
        try {
            jsonOutput = response.toJson();
        } catch (Exception e) {
            logger.error("error handling print requests", e);
            jsonOutput = "{\"error\":\"Error converting response to json\",\"messages\":null}";
        }
        return jsonOutput;
    }

    void printRequest(PrintRequest request, PrintResponse response) {
        FileInputStream inputStream = null;
        File pdfFile = null;
        try {
            PickList pickList = new PickList(request);
            pdfFile = pickList.render();
            if (pdfFile != null) {
                inputStream = new FileInputStream(pdfFile);
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
        }
        if (inputStream == null) {
            response.setError(PrintResponse.ERROR_PROBLEM_CREATING_PRINT_OUTPUT);
        } else {
            // set up the print document
            DocFlavor htmlInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc myDoc = new SimpleDoc(inputStream, htmlInFormat, null);
            PrintRequestAttributeSet aSet = new HashPrintRequestAttributeSet();
            aSet.add(new Copies(1));
            // try to get the default printer
            javax.print.PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            if (service == null) {
                response.setError(PrintResponse.ERROR_COULD_NOT_LOOKUP_DEFAULT_PRINTER);
            } else {
                // create a new print job
                DocPrintJob job = service.createPrintJob();
                try {
                    // submit the print job
                    job.print(myDoc, aSet);
                } catch (PrintException e) {
                    logger.error("Error trying to submit print job", e);
                }
            }
            try {
                inputStream.close();
                boolean fileDeleted = pdfFile.delete();
                if (!fileDeleted) {
                    logger.error("Could not delete print file: " + pdfFile.getCanonicalPath());
                }
            } catch (IOException e) {
                logger.error("Error trying to close or delete the print file", e);
            }
        }
    }

    void validateRequest(PrintRequest request, PrintResponse response) {
        ArrayList<Message> messages = new ArrayList<>();
        boolean errors = false;
        do {
            if (request == null) {
                errors = true;
                messages.add(Message.createError(PrintResponse.ERROR_EMPTY_REQUEST));
                break;
            }
            if (request.isTestFail() || request.isTestSuccess()) {
                if (request.isTestFail()) {
                    errors = true;
                }
            } else {
                if (request.getProducts() == null || request.getProducts().length == 0) {
                    errors = true;
                    messages.add(Message.createError(PrintResponse.ERROR_EMPTY_PRODUCTS_ARRAY));
                    break;
                }
                for (Product product : request.getProducts()) {
                    if (!validProduct(product, messages)) {
                        errors = true;
                    }
                }
            }
        } while (false);
        if (errors) {
            response.setError(PrintResponse.ERROR_VALIDATIONS);
        }
        if (messages.size() > 0) {
            response.setMessages(messages.toArray(new Message[messages.size()]));
        }
    }

    boolean validProduct(Product product, ArrayList<Message> messages) {
        boolean retVal = true;
        if (StringUtils.isBlank(product.getBarCode())) {
            messages.add(Message.createWarning(PrintResponse.WARN_BAR_CODE_IS_BLANK));
        }
        if (StringUtils.isBlank(product.getColor())) {
            messages.add(Message.createWarning(PrintResponse.WARN_COLOR_IS_BLANK));
        }
        if (StringUtils.isBlank(product.getDescription())) {
            messages.add(Message.createWarning(PrintResponse.WARN_DESCRIPTION_IS_BLANK));
        }
        if (product.getImageURL() == null) {
            messages.add(Message.createWarning(PrintResponse.WARN_IMAGE_URL_IS_BLANK));
        }
        BigDecimal price = product.getExtendedPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) != 1) {
            messages.add(Message.createError(PrintResponse.ERROR_EXTENDED_PRICE_IS_INVALID));
        }
        price = product.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) != 1) {
            messages.add(Message.createError(PrintResponse.ERROR_PRICE_IS_INVALID));
        }
        if (product.getQuantity() == null || product.getQuantity().compareTo(BigInteger.ZERO) != 1) {
            messages.add(Message.createError(PrintResponse.ERROR_QUANTITY_IS_INVALID));
        }
        if (StringUtils.isBlank(product.getNumber())) {
            retVal = false;
            messages.add(Message.createError(PrintResponse.ERROR_PRODUCT_NUMBER_CAN_NOT_BE_BLANK));
        }
        if (StringUtils.isBlank(product.getBrand())) {
            retVal = false;
            messages.add(Message.createError(PrintResponse.ERROR_PRODUCT_BRAND_CAN_NOT_BE_BLANK));
        }
        if (StringUtils.isBlank(product.getName())) {
            retVal = false;
            messages.add(Message.createError(PrintResponse.ERROR_PRODUCT_NAME_CAN_NOT_BE_BLANK));
        }
        if (StringUtils.isBlank(product.getSize())) {
            messages.add(Message.createWarning(PrintResponse.WARN_SIZE_IS_BLANK));
        }
        return retVal;
    }
}

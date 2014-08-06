package com.eastbay.performancezone.print.Response;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/18/14
 * Time: 3:16 PM
 * SDG
 */
public class PrintResponse {
    public static final String ERROR_UNKNOWN_ERROR = "Unknown error!";
    public static final String ERROR_EMPTY_REQUEST = "Empty request!";
    public static final String ERROR_EMPTY_PRODUCTS_ARRAY = "Empty products array!";
    public static final String ERROR_VALIDATIONS = "Request validation errors!";
    public static final String ERROR_EXTENDED_PRICE_IS_INVALID = "ExtendedPrice is invalid!";
    public static final String ERROR_PRICE_IS_INVALID = "Price is invalid!";
    public static final String ERROR_QUANTITY_IS_INVALID = "Quantity is invalid!";
    public static final String ERROR_PRODUCT_BRAND_CAN_NOT_BE_BLANK = "Product brand can not be blank!";
    public static final String ERROR_PRODUCT_NAME_CAN_NOT_BE_BLANK = "Product name can not be blank!";
    public static final String ERROR_PRODUCT_NUMBER_CAN_NOT_BE_BLANK = "Product number can not be blank!";
    public static final String ERROR_PROBLEM_CREATING_PRINT_OUTPUT = "Problem creating the print output.";
    public static final String ERROR_COULD_NOT_LOOKUP_DEFAULT_PRINTER = "Could not lookup the default printer!";
    public static final String WARN_BAR_CODE_IS_BLANK = "Bar code is blank!";
    public static final String WARN_COLOR_IS_BLANK = "Color is blank!";
    public static final String WARN_DESCRIPTION_IS_BLANK = "Description is blank!";
    public static final String WARN_IMAGE_URL_IS_BLANK = "ImageURL is blank!";
    public static final String WARN_SIZE_IS_BLANK = "Size is blank!";
    private Message[] messages;
    private String error = null;

    public static PrintResponse fromJsonToPrintResponse(String json) {
        PrintResponse intoPrintResponse = null;
        if (json != null) {
            intoPrintResponse = new JSONDeserializer<PrintResponse>().use(null, PrintResponse.class).deserialize(json);
        }
        return intoPrintResponse;
    }

    public static Collection<PrintResponse> fromJsonArrayToPrintResponses(String json) {
        return new JSONDeserializer<List<PrintResponse>>().use(null, ArrayList.class).use("values", PrintResponse.class).deserialize(json);
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public String toJson() {
        return new JSONSerializer().include("messages").exclude("*.class").serialize(this);
    }
}

package com.eastbay.performancezone.print.content;

import com.eastbay.performancezone.print.Request.PrintRequest;
import com.eastbay.performancezone.print.Request.Product;
import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/23/14
 * Time: 1:40 PM
 * SDG
 */
public class PickList {

    public final static String TEMPLATE_PATH = "/var/opt/PerformanceZone/PrintTemplates/ReceiptTemplate.pdf";
    private static Logger logger = Logger.getLogger(PickList.class);
    private PrintRequest request = null;

    public PickList(PrintRequest request) {
        this.request = request;
    }

    int calculatePageCount(int itemCount) {
        int pageCount = itemCount / 2;
        if (itemCount % 2 == 1) {
            pageCount++;
        }
        return pageCount;
    }

    void drawString(PDPageContentStream contentStream, PDFont font, int fontSize, int x, int y, String string) throws IOException {
        drawString(contentStream, font, Color.BLACK, fontSize, x, y, string);
    }

    void drawString(PDPageContentStream contentStream, PDFont font, Color color, int fontSize, int x, int y, String string) throws IOException {
        if (string != null) {
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(x, y);
            contentStream.drawString(string);
            contentStream.endText();
        }
    }

    public File render() {
        Date now = new Date();
        SimpleDateFormat fmtMMddyyyyhhmmaa = new SimpleDateFormat("MM/dd/yyyy hh:mmaa");
        File pdfDocument = null;
        PDDocument document;
        PDPage page;
        PDPageContentStream contentStream;
        int itemCount = request.getProducts().length;
        int pageCount = calculatePageCount(itemCount);
        int itemNumber;
        // get the list total
        BigDecimal listTotal = BigDecimal.ZERO;
        for (Product product : request.getProducts()) {
            listTotal = listTotal.add(product.getExtendedPrice()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }
        try {
            // start with the template
            document = PDDocument.load(TEMPLATE_PATH);

            // add pages if we need to....
            page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
            if (request.getProducts().length > 2) {
                for (int i = 1; i < pageCount; i++) {
                    document.importPage(page);
                }
            }

            // load the fonts
            PDFont fontHelvetica = PDType1Font.HELVETICA;
            PDFont fontHelveticaBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontSourceSansProLight = null;
            PDFont fontSourceSansProBold = null;
            PDFont fontSourceSansProBlack = null;
            InputStream inputStream = null;
            try {
                inputStream = getClass().getResourceAsStream("/fonts/source-sans-pro/SourceSansPro-Black.ttf");
                if (inputStream != null) {
                    fontSourceSansProBlack = PDTrueTypeFont.loadTTF(document, inputStream);
                    inputStream.close();
                    inputStream = null;
                }
                inputStream = getClass().getResourceAsStream("/fonts/source-sans-pro/SourceSansPro-Bold.ttf");
                if (inputStream != null) {
                    fontSourceSansProBold = PDTrueTypeFont.loadTTF(document, inputStream);
                    inputStream.close();
                    inputStream = null;
                }
                inputStream = getClass().getResourceAsStream("/fonts/source-sans-pro/SourceSansPro-Light.ttf");
                if (inputStream != null) {
                    fontSourceSansProLight = PDTrueTypeFont.loadTTF(document, inputStream);
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException e) {
                logger.error("problem loading fonts", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error("problem loading fonts", e);
                    }
                }
            }
            if (fontSourceSansProLight == null) {
                logger.info("substituting Helvetica for Light...");
                fontSourceSansProLight = fontHelvetica;
            }
            if (fontSourceSansProBold == null) {
                fontSourceSansProBold = fontHelveticaBold;
                logger.info("substituting Helvetica Bold for Bold...");
            }
            if (fontSourceSansProBlack == null) {
                fontSourceSansProBlack = fontHelveticaBold;
                logger.info("substituting Helvetica Bold for Black...");
            }

            // print the page
            Product product;
            int x;
            int y;
            int totalX = 518;
            int height = 14;
            int imageSize = 165;
            int barCodeLength = 70;
            int barCodeHeight = 38;
            for (int pageNumber = 0; pageNumber < pageCount; pageNumber++) {
                page = (PDPage) document.getDocumentCatalog().getAllPages().get(pageNumber);
                // Start a new content stream which will "hold" the to be created content
                contentStream = new PDPageContentStream(document, page, true, true, true);

                // print date
                drawString(contentStream, fontSourceSansProLight, Color.WHITE, 14, 63, 602, fmtMMddyyyyhhmmaa.format(now));
                // print top total
                drawString(contentStream, fontSourceSansProBlack, Color.WHITE, 18, 518, 602, "$" + listTotal);
                // print bottom total
                drawString(contentStream, fontSourceSansProBlack, 18, totalX, 128, "$" + listTotal);
                // print page x of y
                drawString(contentStream, fontSourceSansProLight, 10, 562, 17, (pageNumber + 1) + " of " + pageCount);

                for (int pageProduct = 0; pageProduct < 2; pageProduct++) {
                    x = 231;
                    y = 538;
                    itemNumber = pageNumber * 2;
                    switch (pageProduct) {
                        case (0):
                            break;
                        case (1):
                            itemNumber++;
                            y = 350;
                            break;
                    }
                    if (itemNumber < request.getProducts().length) {
                        product = request.getProducts()[itemNumber];

                        // divider line
                        contentStream.drawLine(212, y - (10 * height), 214, (float) (y + (1.5 * height)));

                        // brand
                        drawString(contentStream, fontSourceSansProBold, 12, x, y, product.getBrand());
                        // extended
                        drawString(contentStream, fontSourceSansProBlack, 18, totalX, y - 10, "$" + product.getExtendedPrice());
                        // name
                        y = y - height;
                        drawString(contentStream, fontSourceSansProBold, 12, x, y, product.getName());
                        // number
                        y = y - height;
                        drawString(contentStream, fontSourceSansProLight, 12, x, y, "Product Number: " + product.getNumber());
                        // size
                        y = y - height;
                        drawString(contentStream, fontSourceSansProLight, 12, x, y, "Size: " + product.getSize());
                        // color
                        y = y - height;
                        drawString(contentStream, fontSourceSansProLight, 12, x, y, product.getColor());
                        // quantity & price
                        y = y - height;
                        drawString(contentStream, fontSourceSansProLight, 12, x, y, product.getQuantity() + " x " + product.getPrice());

                        y = y - (5 * height);
                        // product image
                        if (product.getImageURL() != null) {
                            int timeOutSeconds = 3;
                            try {
                                URL url = new URL(product.getImageURL());
                                URLConnection conn = url.openConnection();
                                conn.setConnectTimeout(timeOutSeconds * 1000);
                                conn.connect();
                                BufferedImage image = ImageIO.read(conn.getInputStream());
                                PDXObjectImage productImage = new PDPixelMap(document, image);
                                contentStream.drawXObject(productImage, 30, y, imageSize, imageSize);
                            } catch (MalformedURLException e) {
                                logger.error("Problem with image url: " + product.getImageURL(), e);
                            } catch (IOException e) {
                                logger.error("Problem downloading image, url: " + product.getImageURL(), e);
                            }
                        }

                        // product bar code
                        if (product.getBarCode() != null) {
                            File barCodeFile = createBarCode(product);
                            if (barCodeFile != null) {
                                FileInputStream in = null;
                                try {
                                    in = new FileInputStream(barCodeFile);
                                    PDXObjectImage productImage = new PDJpeg(document, in);
                                    contentStream.drawXObject(productImage, x, y + height, barCodeLength, barCodeHeight);
                                } catch (IOException e) {
                                    logger.error("Problem adding bar code image, barcode: " + product.getBarCode() + ", product: " + product.getNumber(), e);
                                } finally {
                                    if (in != null) {
                                        in.close();
                                    }
                                    // rem next line for testing
                                    boolean fileDeleted = barCodeFile.delete();
                                    if (!fileDeleted) {
                                        logger.error("Could not delete print file: " + barCodeFile.getCanonicalPath());
                                    }
                                }
                            }
                        }
                    }
                }

                // Make sure that the content stream is closed:
                contentStream.close();
            }

            // Save the results and ensure that the document is properly closed:
            pdfDocument = File.createTempFile("printService-", ".pdf");
            document.save(pdfDocument.getCanonicalPath());
            // next line is for testing
            //document.save("printService.pdf");
            document.close();
            // next line is for testing
            //logger.error("pdf: " + pdfDocument.getCanonicalPath());
        } catch (IOException | COSVisitorException e) {
            logger.error("Problem creating pdf document....", e);
        }
        return pdfDocument;
    }

    File createBarCode(Product product) {
        Interleaved2Of5Bean barCodeBean = new Interleaved2Of5Bean();
        int dpi = 600;
        barCodeBean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
        barCodeBean.setWideFactor(3);
        barCodeBean.doQuietZone(false);
        barCodeBean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        File barCodeFile = null;
        OutputStream out = null;
        try {
            barCodeFile = File.createTempFile("printService-", ".jpg");
            // next line is for testing
            //barCodeFile = new File("printService-barCode.jpg");
            out = new FileOutputStream(barCodeFile);

            //Set up the canvas provider for monochrome JPEG output
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    out, "image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

            //Generate the barcode
            barCodeBean.generateBarcode(canvas, product.getBarCode());

            //Signal end of generation
            canvas.finish();
        } catch (IOException e) {
            logger.error("Problem creating bar code: " + product.getBarCode() + ", product: " + product.getNumber());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("Problem creating bar code: " + product.getBarCode() + ", product: " + product.getNumber());
                }
            }
        }
        return barCodeFile;
    }
}

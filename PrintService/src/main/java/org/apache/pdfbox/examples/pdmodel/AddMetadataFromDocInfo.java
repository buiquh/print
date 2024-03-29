/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.examples.pdmodel;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.XMPSchemaBasic;
import org.apache.jempbox.xmp.XMPSchemaDublinCore;
import org.apache.jempbox.xmp.XMPSchemaPDF;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.util.GregorianCalendar;

/**
 * This is an example on how to add metadata to a document.
 * <p/>
 * Usage: java org.apache.pdfbox.examples.pdmodel.AddMetadataToDocument &lt;input-pdf&gt; &lt;output-pdf&gt;
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class AddMetadataFromDocInfo {
    private AddMetadataFromDocInfo() {
        //utility class
    }

    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     * @throws Exception If there is an error parsing the document.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage();
        } else {
            PDDocument document = null;

            try {
                document = PDDocument.load(args[0]);
                if (document.isEncrypted()) {
                    System.err.println("Error: Cannot add metadata to encrypted document.");
                    System.exit(1);
                }
                PDDocumentCatalog catalog = document.getDocumentCatalog();
                PDDocumentInformation info = document.getDocumentInformation();

                XMPMetadata metadata = new XMPMetadata();

                XMPSchemaPDF pdfSchema = metadata.addPDFSchema();
                pdfSchema.setKeywords(info.getKeywords());
                pdfSchema.setProducer(info.getProducer());

                XMPSchemaBasic basicSchema = metadata.addBasicSchema();
                basicSchema.setModifyDate(info.getModificationDate());
                basicSchema.setCreateDate(info.getCreationDate());
                basicSchema.setCreatorTool(info.getCreator());
                basicSchema.setMetadataDate(new GregorianCalendar());

                XMPSchemaDublinCore dcSchema = metadata.addDublinCoreSchema();
                dcSchema.setTitle(info.getTitle());
                dcSchema.addCreator("PDFBox");
                dcSchema.setDescription(info.getSubject());

                PDMetadata metadataStream = new PDMetadata(document);
                metadataStream.importXMPMetadata(metadata);
                catalog.setMetadata(metadataStream);

                document.save(args[1]);
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }
    }

    /**
     * This will print the usage for this document.
     */
    private static void usage() {
        System.err.println("Usage: java org.apache.pdfbox.examples.pdmodel.AddMetadataFromDocInfo " +
                "<input-pdf> <output-pdf>");
    }
}

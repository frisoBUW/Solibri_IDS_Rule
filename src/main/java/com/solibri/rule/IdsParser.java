package com.solibri.rule;

import de.buildingsmart.ids.Ids;
import de.buildingsmart.ids.SpecificationType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class IdsParser {

    public Ids parseIdsFile(String filename) {
        Ids ids = null;
        try {
            // Disable JAXB optimizations to fix error with JDK 9+ versions
            System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");

            // Create the JAXBContext
            JAXBContext jaxbContext = JAXBContext.newInstance(Ids.class);

            // Create the Unmarshaller
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Use the Unmarshaller to unmarshal the IDS XML file
            ids = (Ids) unmarshaller.unmarshal(new File(filename));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return ids;
    }

//    private void setUp(Ids ids) {
//
//        // Extract and use the Info and Specifications
//        Ids.Info info = ids.getInfo();
//        Ids.Specifications specifications = ids.getSpecifications();
//

//        // You can now use the info and specifications objects in your application
//        printInfoTitle(info);
//        printSpecificationsClass(specifications);
//        printSpecificationDetails(ids);
//    }

//    private void printInfoTitle(Ids.Info info) {
//        System.out.println("Title: " + info.getTitle());
//    }
//
//    private void printSpecificationsClass(Ids.Specifications specifications) {
//        System.out.println("Specifications class: " + specifications.getClass().getName());
//    }
//
//    private void printSpecificationDetails(Ids ids) {
//        for (SpecificationType specification : ids.getSpecifications().getSpecification()) {
//            String name = specification.getName();
//            String entity = specification.getApplicability().getEntity().getName().getSimpleValue();
//            System.out.println("Specification: " + name + " -- Entity: " + entity);
//        }
//    }
//
//    public static void main(String[] args) {
//        String filename = "C:\\Projects\\_Java\\Solibri\\view-examples\\src\\main\\resources\\IDS_Test_5D.xml";
//
//        IdsParser idsParser = new IdsParser(filename);
//        Ids ids = idsParser.parseIdsFile();
//
//        // Extract and use the Info and Specifications
//        Ids.Info info = ids.getInfo();
//        Ids.Specifications specifications = ids.getSpecifications();
//
//    }
}
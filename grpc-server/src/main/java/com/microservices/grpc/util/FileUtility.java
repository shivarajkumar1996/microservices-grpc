package com.microservices.grpc.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.microservices.grpc.config.FileStorageProperties;
import com.microservices.grpc.constants.CommonConstants;
import com.microservices.grpc.exceptions.FileParsingException;
import com.microservices.grpc.exceptions.FileStorageException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import com.microservices.grpc.pojo.Response;
import com.microservices.grpc.pojo.UserPojo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@Log4j2
@Component
public class FileUtility {

    private final Path fileStorageDir;

    @Autowired
    public FileUtility(FileStorageProperties fileStorageProperties) {
        this.fileStorageDir = Path.of(fileStorageProperties.getStorageDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageDir);
        } catch (Exception ex) {
            log.error("Error creating storage dir on the server. Exception: {}", ex);
            throw new FileStorageException("Error occurred while creating the file storage directory.",  Map.of("storage_dir", this.fileStorageDir.toAbsolutePath().toString(), "message", "Error occurred while creating the file storage directory.", "exception", ex.toString()));
        }
    }
    public UserPojo findById(String id){
        String fileName = getFileNameForId(id);
        if(fileName == null){
            return null;
        }
        UserPojo userPojo = parseFile(id, fileName);

        return userPojo;

    }

    public String getFileNameForId(String id){

        File directory = new File(fileStorageDir.toString());
        FileFilter filter = new FileFilter(id + ".");
        String[] fileList = directory.list(filter);

        if (fileList == null || fileList.length == 0) {
            log.info("Resource not found: Id: {}", id);
            return null;
        }

        if(fileList.length > 1){
            log.warn("Multiple files detected for the user with id: {}. This is not an expected behaviour. Pick the first occurrence of the file name.", id);
        }
        return Path.of(fileStorageDir.toString(),fileList[0] ).toAbsolutePath().toString();
    }

    public boolean create(UserPojo userPojo, String fileType) throws IOException, JAXBException {

        if(fileType.toLowerCase().equals(CommonConstants.CSV_FILE_EXTENSION)){
            createCSVFile(userPojo);
        }else{
            createXMLFile(userPojo);
        }
        return true;
    }
    public boolean save(UserPojo userPojo) throws IOException, JAXBException {

        String fileName = getFileNameForId(String.valueOf(userPojo.getId()));
        if(fileName != null) {
            log.debug("Found existing user file: {}", fileName);
            String extension = FilenameUtils.getExtension(fileName);
            if (extension.equals(CommonConstants.CSV_FILE_EXTENSION)) {
                createCSVFile(userPojo);
            } else {
                createXMLFile(userPojo);
            }
        } else{
           createCSVFile(userPojo);
        }
        return true;
    }

    public UserPojo createCSVFile(UserPojo userPojo) throws IOException {
        File csvOutputFile = new File(fileStorageDir.toString() + "\\" + userPojo.getId() + "." +CommonConstants.CSV_FILE_EXTENSION);
        writeToCSV(userPojo, csvOutputFile);
        log.info("Successfully created user file with id: {}", userPojo.getId());
        return userPojo;

    }

    public boolean writeToCSV(UserPojo userPojo, File csvOutputFile) throws IOException {

        CsvMapper mapper = new CsvMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

        CsvSchema schema = CsvSchema.builder().setUseHeader(true)
                .addColumn(CommonConstants.ID_COLUMN)
                .addColumn(CommonConstants.NAME_COLUMN)
                .addColumn(CommonConstants.DOB_COLUMN)
                .addColumn(CommonConstants.SALARY_COLUMN)
                .build();

        ObjectWriter writer = mapper.writerFor(UserPojo.class).with(schema);

        writer.writeValues(csvOutputFile).writeAll(Collections.singleton(userPojo));

        return true;

    }

    public UserPojo createXMLFile(UserPojo userPojo) throws IOException, JAXBException {
        File xmlOutputFile = new File(fileStorageDir.toString() + "\\" + userPojo.getId() + "." +CommonConstants.XML_FILE_EXTENSION);
        writeToXML(userPojo, xmlOutputFile);
        log.info("Successfully created user file with id: {}", userPojo.getId());
        return userPojo;

    }

    public boolean writeToXML(UserPojo userPojo, File xmlOutputFile) throws IOException, JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(UserPojo.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to File
        m.marshal(userPojo,xmlOutputFile);
        return true;

    }
    public UserPojo parseFile(String id, String fileName){
        UserPojo userPojo = null;
        String extension = FilenameUtils.getExtension(fileName);
        if(extension.equals(CommonConstants.CSV_FILE_EXTENSION)){
            userPojo = parseCSVFile(id, fileName);
        }else{

            userPojo = parseXMLFile(id, fileName);
        }
        return userPojo;
    }


    public UserPojo parseCSVFile(String id, String fileName){
        UserPojo userPojo = null;
        try(
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(br);
        ) {
            for(CSVRecord record : parser) {
                userPojo = new UserPojo();
                userPojo.setId(Integer.parseInt(record.get(CommonConstants.ID_COLUMN)));
                userPojo.setName(record.get(CommonConstants.NAME_COLUMN));
                userPojo.setDob(record.get(CommonConstants.DOB_COLUMN));
                userPojo.setSalary(Double.parseDouble(record.get(CommonConstants.SALARY_COLUMN)));
                break;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource not found.",  Map.of("id", id, "message", "Resource Not Found"));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FileParsingException("Error occurred while parsing the file.",  Map.of("file", fileName, "message", "Error occurred while parsing the file.", "exception", e.toString()));
        }
        return userPojo;

    }

    public UserPojo parseXMLFile(String id, String fileName){
        File xmlFile = new File(fileName);
        UserPojo userPojo = null;
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(UserPojo.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            userPojo = (UserPojo) jaxbUnmarshaller.unmarshal(xmlFile);

        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            throw new FileParsingException("Error occurred while parsing the file.",  Map.of("file", fileName, "message", "Error occurred while parsing the file.", "exception", e.toString()));
        }

        return userPojo;


    }
}

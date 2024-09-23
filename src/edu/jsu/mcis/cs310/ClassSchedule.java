package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
    // NEW JSON OBJECT
    JsonObject json = new JsonObject();

    // JSONOBJECT SCHEDULETYPE
    JsonObject scheduletype = new JsonObject();

    // JSONOBJECT SUBJECT
    JsonObject subject = new JsonObject();

    // JSONOBJECT COURSE
    JsonObject course = new JsonObject();

    // JSONARRAY SECTION
    JsonArray section = new JsonArray();

    // THE SET OF POSSIBLE VALUES HAS ONLY ONE ELEMENT
    String jsonString = null;

    // STRING ITERATOR
    Iterator<String[]> iterator = csv.iterator();

    // STRING HEADERROW
    String[] headerRow = iterator.next();

    // HASHMAP TO MAP HEADER NAMES TO COLUMN INDEXES
    HashMap<String, Integer> headers = new HashMap<>();

    // FOR LOOP TO MAP HEADERS
    for (int i = 0; i < headerRow.length; ++i) {
        headers.put(headerRow[i], i);
    }

    // WHILE LOOP TO ITERATE THROUGH THE CSV ROWS
    while (iterator.hasNext()) {
        // Initialize 'row' correctly here
        String[] row = iterator.next();

        // STRINGS TO EXTRACT DATA FROM THE ROW
        String NumberOField = row[headers.get(NUM_COL_HEADER)];
        String SubjectField = row[headers.get(SUBJECT_COL_HEADER)];
        String TypeField = row[headers.get(TYPE_COL_HEADER)];
        String ScheduleField = row[headers.get(SCHEDULE_COL_HEADER)];
        String CrnField = row[headers.get(CRN_COL_HEADER)];
        String SectionField = row[headers.get(SECTION_COL_HEADER)];
        String StartTime = row[headers.get(START_COL_HEADER)];
        String EndTime = row[headers.get(END_COL_HEADER)];
        String Days = row[headers.get(DAYS_COL_HEADER)];
        String Where = row[headers.get(WHERE_COL_HEADER)];

        // SPLIT THE INSTRUCTOR COLUMN INTO MULTIPLE NAMES
        String[] instructors = row[headers.get(INSTRUCTOR_COL_HEADER)].split(",");

        // STRING NUMBER ARRAY FOR SPLITTING NUMBER AND SUBJECT ID
        String[] numArray = NumberOField.split(" ");
        String SubjectidField = numArray[0];
        String NumField = numArray[1];

        // SUBJECT AND SCHEDULE FIELD AND VALUES
        subject.put(SubjectidField, SubjectField);
        scheduletype.put(TypeField, ScheduleField);

        // COURSE ELEMENT: COURSE FACTOR FOR SUBJECTID, NUMBER, DESCRIPTION
        JsonObject CourseDetail = new JsonObject();
        CourseDetail.put(SUBJECTID_COL_HEADER, SubjectidField);
        CourseDetail.put(NUM_COL_HEADER, NumField);
        CourseDetail.put(DESCRIPTION_COL_HEADER, row[headers.get(DESCRIPTION_COL_HEADER)]);

        // INTEGER VALUE FOR CREDITS COLUMN
        CourseDetail.put(CREDITS_COL_HEADER, Integer.valueOf(row[headers.get(CREDITS_COL_HEADER)]));

        // ADD THE COURSE ELEMENT
        course.put(NumberOField, CourseDetail);

        // JSONOBJECT SECTION ELEMENT
        JsonObject SectionDetail = new JsonObject();

        // SECTION ELEMENT CRN COLUMN VALUE OF EACH ROW
        SectionDetail.put(CRN_COL_HEADER, Integer.valueOf(row[headers.get(CRN_COL_HEADER)]));
        SectionDetail.put(SUBJECTID_COL_HEADER, SubjectidField);
        SectionDetail.put(NUM_COL_HEADER, NumField);
        SectionDetail.put(SECTION_COL_HEADER, SectionField);
        SectionDetail.put(TYPE_COL_HEADER, TypeField);
        SectionDetail.put(START_COL_HEADER, StartTime);
        SectionDetail.put(END_COL_HEADER, EndTime);
        SectionDetail.put(DAYS_COL_HEADER, Days);
        SectionDetail.put(WHERE_COL_HEADER, Where);

        // LIST FOR INSTRUCTORS
        List<String> instructorsList = new ArrayList<>();

        // FOR LOOP FOR INSTRUCTORS
        for (String instructor : instructors) {
            instructorsList.add(instructor.trim());
        }

        // ADD INSTRUCTOR TO SECTION DETAIL
        SectionDetail.put(INSTRUCTOR_COL_HEADER, instructorsList);

        // ADD THE SECTION TO THE SECTION ARRAY
        section.add(SectionDetail);

        // ADD SUBJECT, SCHEDULETYPE, COURSE, SECTION TO THE MAIN JSON OBJECT
        json.put("subject", subject);
        json.put("scheduletype", scheduletype);
        json.put("course", course);
        json.put("section", section);
    }

    // CONVERTING AN OBJECT INTO A STRING
    jsonString = Jsoner.serialize(json);
    return jsonString;
}
    
    public String convertJsonToCsvString(JsonObject Datajson) {
        
        // DECLARED AS JSONOBJECT DATAJSON
        JsonObject datajson = new JsonObject(Datajson);

        // Extracting JSON objects for schedule types, subjects, courses, and sections
        JsonObject scheduleTypes = (JsonObject) datajson.get("scheduletype");
        JsonObject subjects = (JsonObject) datajson.get("subject");
        JsonObject courses = (JsonObject) datajson.get("course");
        JsonArray sections = (JsonArray) datajson.get("section");

        // STRING CSVLINE FOR NEW ARRAYLIST
        List<String[]> csvLines = new ArrayList<>();

        // STRING CSVHEADER
        String[] csvHeader = {
            CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER,
            SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER,
            END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER
        };

        // ADDING CSV HEADER TO CSV LINES
        csvLines.add(csvHeader);

        // FOR LOOP FOR SECTIONS
        for (int i = 0; i < sections.size(); i++) {
            // Get the current section as a JsonObject
            JsonObject currentSection = (JsonObject) sections.get(i);

            // Get the instructor array from the current section
            JsonArray instructorArray = (JsonArray) currentSection.get(INSTRUCTOR_COL_HEADER);

            // Convert instructor array to String array
            String[] instructorNames = instructorArray.toArray(new String[0]);

            // Join multiple instructor names into a single string
            String instructors = String.join(", ", instructorNames);

            // HashMap to store course details
            HashMap courseDetails = (HashMap) courses.get(
            currentSection.get(SUBJECTID_COL_HEADER) + " " + currentSection.get(NUM_COL_HEADER)
            );

            // Create the CSV line
            String[] csvLine = {
                currentSection.get(CRN_COL_HEADER).toString(),
                subjects.get(currentSection.get(SUBJECTID_COL_HEADER)).toString(),
                (currentSection.get(SUBJECTID_COL_HEADER) + " " + currentSection.get(NUM_COL_HEADER)),
                courseDetails.get(DESCRIPTION_COL_HEADER).toString(),
                currentSection.get(SECTION_COL_HEADER).toString(),
                currentSection.get(TYPE_COL_HEADER).toString(),
                courseDetails.get(CREDITS_COL_HEADER).toString(),
                currentSection.get(START_COL_HEADER).toString(),
                currentSection.get(END_COL_HEADER).toString(),
                currentSection.get(DAYS_COL_HEADER).toString(),
                currentSection.get(WHERE_COL_HEADER).toString(),
                scheduleTypes.get(currentSection.get(TYPE_COL_HEADER).toString()).toString(),
                instructors
            };

            // Add the CSV line to csvLines
            csvLines.add(csvLine);
        }

        // Create a StringWriter to hold the CSV output
        StringWriter csvStringWriter = new StringWriter();

        // Create a CSVWriter with tab as delimiter, and use the default escape and line end
        CSVWriter csvWriter = new CSVWriter(csvStringWriter, '\t', '"', '\\', "\n");

        // Write all CSV lines to the CSVWriter
        csvWriter.writeAll(csvLines);

        // Return the CSV string
        return csvStringWriter.toString();

        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}
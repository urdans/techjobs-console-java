package org.launchcode.techjobs.console;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "resources/job_data.csv";
    private static Boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;
    private static ArrayList<HashMap<String, String>> allJobsCopy = null;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }
        //5 ways to do the same sort
//        values.sort((o1, o2) -> o1.compareTo(o2));
        values.sort(null);
//        values.sort(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });
//        values.sort((String o1, String o2) -> o1.compareTo(o2));
//        values.sort((o1, o2) -> {
//            return o1.compareTo(o2);
//        });

        return values;
    }

    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();
//        return new ArrayList<HashMap<String, String>>(allJobs);  //this one doesn't clone, it just make a shallow copy
//        return allJobs;
        return cloneAllJobs(); //this one works, unintentionally!!!
    }

    public static ArrayList<HashMap<String, String>> cloneAllJobs(){
        if(allJobsCopy==null) {
            allJobsCopy = new ArrayList<>();
        }else{
            allJobsCopy.clear();
        }

        for (HashMap<String,String> row: allJobs) {
            HashMap<String, String> jobCopy = new HashMap<>();
            for (Map.Entry<String, String> field: row.entrySet()) {
                String fieldName = field.getKey();
                String fieldData = field.getValue();
                jobCopy.put(fieldName, fieldData);
            }
            allJobsCopy.add(jobCopy);
        }

        return allJobsCopy;
    }


    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of teh field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();
        value = value.toLowerCase();
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {

            String aValue = row.get(column).toLowerCase();

            if (aValue.contains(value)) {
                jobs.add(row);
            }
        }

        return jobs;
    }

    /* finds a given string within the entire record */
    public static ArrayList<HashMap<String, String>> findByValue(String word){
        // load data, if not already loaded
        loadData();
        word = word.toLowerCase();
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();
        for (HashMap<String,String> row: allJobs) {
            for (Map.Entry<String, String> fieldData: row.entrySet()) {
                String fieldDataLowerCase = fieldData.getValue().toLowerCase();
                if(fieldDataLowerCase.contains(word)){
                    jobs.add(row);
                    break;
                }
            }
        }

        return jobs;
    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}

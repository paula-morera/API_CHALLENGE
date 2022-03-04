package Cases;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Methods {
    public boolean compare(String expires_at){
        Timestamp tm1,tm2;
        tm1=Timestamp.valueOf(expires_at.substring(0,19));
        LocalDateTime local=LocalDateTime.now(ZoneOffset.UTC);
        tm2=Timestamp.valueOf(local);
        return tm1.compareTo(tm2) < 0;

    }
    public Object[][] RFile(String path) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path), ',', '"' , 0);
        List<String[]> allRows = reader.readAll();
        Object[][] obj = new Object[allRows.size()][];
        for(int i=0; i < allRows.size();i++){
            obj[i]= allRows.get(i);
        }
        reader.close();
        return obj;
    }

    public void AddList(ArrayList listName, ArrayList listID) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("./src/test/java/Data/Listas.csv"), ',');
        List<String[]> allRows = reader.readAll();
        Object[] obj = new Object[allRows.size()];
        Object[][] nuevos = new Object[listID.size()][2];
        int cantidadNuevos=0;
        for(int i=0; i < allRows.size();i++){
            obj[i] = allRows.get(i)[1].trim();
        }
        reader.close();

        for(int i=0;i<listID.size();i++){
            if (Arrays.stream(obj).noneMatch(listID.get(i).toString()::equals)){
                nuevos[cantidadNuevos][1]= listID.get(i).toString();
                nuevos[cantidadNuevos][0]= listName.get(i);
                cantidadNuevos++;
            }
        }

        CSVWriter writer = new CSVWriter(new FileWriter("./src/test/java/Data/Listas.csv"),',',CSVWriter.NO_QUOTE_CHARACTER);
        writer.writeAll(allRows);
        for(Object[] val: nuevos){
            System.out.println(val[0] +" "+val[1]);
            writer.writeNext(new String[]{val[0] + ", " + val[1]});
        }
        writer.close();
    }

    public Object[][] DeleteList() throws IOException {
        CSVReader reader = new CSVReader(new FileReader("./src/test/java/Data/Listas.csv"), ',', '"' , 0);
        List<String[]> allRows = reader.readAll();
        Object[][] obj = new Object[1][];
        obj[0]=allRows.get(0);
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter("./src/test/java/Data/Listas.csv"),',',CSVWriter.NO_QUOTE_CHARACTER);
        allRows.remove(0);
        writer.writeAll(allRows);
        writer.close();

        return obj;
    }

    public Object[][] MoviesToAdd() throws IOException {
        CSVReader movies = new CSVReader(new FileReader("./src/test/java/Data/Movies.csv"), ',', '"' , 0);
        List<String[]> allRowsMovies = movies.readAll();
        Object[][] objMovies = new Object[allRowsMovies.size()][4];
        movies.close();
        CSVReader lists = new CSVReader(new FileReader("./src/test/java/Data/Listas.csv"), ',', '"' , 0);
        List<String[]> allRowsList = lists.readAll();
        Object[] objLists = new Object[allRowsList.size()];
        lists.close();
        for(int i=0; i < allRowsList.size();i++){
            objLists[i] = allRowsList.get(i)[0];
        }
        int canMovies=0;
        for (String[] allRowsMovie : allRowsMovies) {
            if (Arrays.asList(objLists).contains(allRowsMovie[0])) {
                objMovies[canMovies][0] = allRowsMovie[0];
                objMovies[canMovies][1] = allRowsList.get(Arrays.asList(objLists).indexOf(allRowsMovie[0]))[1];
                objMovies[canMovies][2] = allRowsMovie[1];
                objMovies[canMovies][3] = allRowsMovie[2];
                canMovies++;
            }
        }

        Object[][] output = new Object[canMovies][];
        System.arraycopy(objMovies, 0, output, 0, canMovies);
        return output;
    }
}

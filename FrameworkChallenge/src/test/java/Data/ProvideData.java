package Data;

import Cases.Methods;
import org.testng.annotations.DataProvider;

import java.io.IOException;


public class ProvideData {

    Methods methods = new Methods();

    @DataProvider(name = "ListNames")
    public Object[][] InfoList() throws IOException {
        return methods.RFile("./src/test/java/Data/ToCreateList.csv");
    }

    @DataProvider(name = "Movies")
    public Object[][] AddMovies() throws IOException {
        return methods.MoviesToAdd();
    }

    @DataProvider(name = "ListToClear")
    public Object[][] ListTC() throws IOException {
        Object[][] obj = new Object[1][];
        obj[0] = methods.RFile("./src/test/java/Data/Listas.csv");
        return  obj;
    }
    @DataProvider(name = "ListToDelete")
    public Object[][] ListDelete() throws IOException {
        return methods.DeleteList();
    }

    @DataProvider(name = "ListDetails")
    public Object[][] ListDetail() throws IOException {
        return methods.RFile("./src/test/java/Data/Listas.csv");
    }

    @DataProvider(name = "MovieDetails")
    public Object[][] MovieDet() throws IOException {
        Object[][] obj= methods.RFile("./src/test/java/Data/Movies.csv");
        Object[][] output = new Object[obj.length][2];
        for(int i=0; i < obj.length;i++){
            output[i][0]=obj[i][1];
            output[i][1]=obj[i][2];
        }
        return output;
    }

    @DataProvider(name = "MovieRate")
    public Object[][] MovieRate(){
        return  new Object[][]{
                {615904,7.00}
        };
    }

}

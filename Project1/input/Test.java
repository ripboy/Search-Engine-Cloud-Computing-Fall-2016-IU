import java.io.*;

public class Test {
    public static void main(String [] args) {

        // The name of the file to open.
        String fileName = "Project1_Input_Data.txt";
        String fileNameop = "Project1_Input_Data_op.txt";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                new BufferedReader(fileReader);

            StringBuilder output = new StringBuilder();

            int count=1;

            while((line = bufferedReader.readLine()) != null) {
              output.append(line+",");
              if(count%10==0)
              {
                output.setLength(output.length() - 1);
                output.append(System.lineSeparator());
              }
              count++;
//              System.out.println(line);
            }

          FileWriter fileWriter =
              new FileWriter(fileNameop);

          // Always wrap FileWriter in BufferedWriter.
          BufferedWriter bufferedWriter =
              new BufferedWriter(fileWriter);

          // Note that write() does not automatically
          // append a newline character.
          bufferedWriter.write(output.toString());

          // Always close files.
          bufferedWriter.close();


            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" +
                fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '"
                + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }
}

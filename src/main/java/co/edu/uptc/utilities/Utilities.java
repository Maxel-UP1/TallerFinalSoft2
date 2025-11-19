package co.edu.uptc.utilities;


//import javafx.collections.ObservableList;
//import javafx.scene.control.ComboBox;
import java.util.ArrayList;
import java.util.Random;

public class Utilities {


    //el metodo quita espacios al principio y al final del nombre.
    public  String cleanNames(String name){
        String cleanName =  name.toLowerCase();
        ArrayList<String> names = new ArrayList<>();
        String aux = "";
        for (int i = 0; i < cleanName.length(); i++){
            if (cleanName.charAt(i) == ' ' && !aux.equals("")){
                names.add(aux);
                aux = "";
                continue;
            }

            if (cleanName.charAt(i) != ' '){
                aux += cleanName.charAt(i);
            }


        }
        if (names.size() == 0) return aux;
        cleanName = names.get(0);
        for (int i = 1; i < names.size(); i++){
            cleanName += " " + names.get(i);
        }

        return cleanName;
    }

    // genera una letra y dos nuemros aleatorios
    public static String generateId() {

        // Letras disponibles: A, B, C, D
        char randomLetter = (char) ('A' + new Random().nextInt(4));

        // Números disponibles: 0-9
        int randomNumber1 = new Random().nextInt(10);
        int randomNumber2 = new Random().nextInt(10);

        // Construir el ID

        return "2024" + randomLetter + randomNumber1 + randomNumber2;

    }

    //valdia minimo 2 numeros una letra mayusucla y una minuscula minio 6 y maximo 15 caracteres
    public boolean validatePassword(String password){

        boolean flagUpper=false;
        boolean flagLower=false;
        int count=0;
        //Number of numbers in the password
        for(int i=0;i<password.length();i++){
            Character character=password.charAt(i);
            if(character.isDigit(character)){
                count++;
            }
        }
        //Password case
        for (int i = 0; i < password.length(); i++) {
            char character = password.charAt(i);
            if (Character.isLetter(character)) {
                if (Character.isUpperCase(character)) {
                    flagUpper = true;
                } else if (Character.isLowerCase(character)) {
                    flagLower = true;
                }
            }
        }
        //check containing all three parameters to be safe
        if(flagUpper && flagLower && count>=2 && password.length()>=6 && password.length()<=15){
            return true;
        }
        return false;
    }

    //validacioens en general


    //public void fillComboVox(ComboBox<String> coboToFill, ObservableList<String> content){
        //coboToFill.setItems(content);


    //}

    // Método para generar un número aleatorio entre min y max
    public int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }




}

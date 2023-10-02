package utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.regex.Pattern;

import exceptions.ValidationException;

public class Validation {

    /**
     * Função para validar o CEP.
     * 
     * Caso tudo ocorra certo, retornará o CEP formatado para inserção.
     * Se houver algum erro será lançado uma excessão.
     * 
     * @param cep
     * 
     * @return String cep
     * 
     * @exception IllegalArgumentException
     * @exception InterruptedException
     * @exception IOException
     */
    public static String validationCEP(String cep) throws IOException, InterruptedException {

        cep = cep.replaceAll("[^0-9]", "");

        if (cep.isBlank() || cep.length() != 8) {
            throw new IllegalArgumentException("CEP inválido");
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://viacep.com.br/ws/%s/json/".formatted(cep)))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Internal server error, try again later");
        }

        if (response.body().contains("error")) {
            throw new IllegalArgumentException("CEP inválido");
        }

        return cep;
    }

    /**
     * Função para validar o CPF.
     * 
     * Caso tudo ocorra certo, retornará o CPF formatado para inserção.
     * Se houver algum erro será lançado uma excessão.
     * 
     * @param cpf
     * 
     * @return String cpf
     * 
     * @exception IllegalArgumentException
     */
    public static String validationCPF(String cpf) {

        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.isBlank() || cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido");
        }

        int result1 = 0, result2 = 0;

        for (int i = 0, aux = 10; i < cpf.length() - 2; i++, aux--) {
            result1 += aux * Integer.parseInt(String.valueOf(cpf.charAt(i)));
        }

        for (int i = 0, aux = 11; i < cpf.length() - 1; i++, aux--) {
            result2 += aux * Integer.parseInt(String.valueOf(cpf.charAt(i)));
        }

        result1 = ((result1 * 10) % 11) == 10 ? 0 : ((result1 * 10) % 11);
        result2 = ((result2 * 10) % 11) == 10 ? 0 : ((result2 * 10) % 11);

        if (!(result1 == Integer.parseInt(String.valueOf(cpf.charAt(9)))
                && result2 == Integer.parseInt(String.valueOf(cpf.charAt(10))))) {
            throw new IllegalArgumentException("CPF inválido");
        }

        return cpf;
    }

    /**
     * Função para validar o CNPJ.
     * 
     * Caso tudo ocorra certo, retornará o CNPJ formatado para inserção.
     * Se houver algum erro será lançado uma excessão.
     * 
     * @param cnpj
     * 
     * @return String cnpj
     * 
     * @exception IllegalArgumentException
     */
    public static String validationCNPJ(String cnpj) {

        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.isBlank() || cnpj.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido");
        }

        int result1 = 0, result2 = 0;
        int[] pesos1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 }, pesos2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        for (int i = 0; i < pesos1.length; i++) {
            result1 += pesos1[i] * Integer.parseInt(String.valueOf(cnpj.charAt(i)));
        }

        result1 = (result1 % 11) < 2 ? 0 : (11 - (result1 % 11));

        for (int i = 0; i < pesos2.length; i++) {
            result2 += pesos2[i] * Integer.parseInt(String.valueOf(cnpj.charAt(i)));
        }

        result2 = (result2 % 11) < 2 ? 0 : (11 - (result2 % 11));

        if (result1 == Integer.parseInt(String.valueOf(cnpj.charAt(12)))
                && result2 == Integer.parseInt(String.valueOf(cnpj.charAt(13)))) {
            throw new IllegalArgumentException("CNPJ inválido");
        }

        return cnpj;
    }

    /**
     * Função para validar a Inscrição Estadual.
     * 
     * Caso tudo ocorra certo, retornará a Inscrição Estadual formatada para
     * inserção.
     * Se houver algum erro será lançado uma excessão.
     * 
     * @param ie
     * 
     * @return String ie
     * 
     * @exception IllegalArgumentException
     */
    public static String validationIE(String ie) {

        ie = ie.replaceAll("[^0-9Pp]", "");

        if (ie.isBlank()) {
            throw new IllegalArgumentException("Inscrição estadual inválida");
        }

        if (ie.contains("P") || ie.contains("p")) {

            if (ie.length() != 14) {
                throw new IllegalArgumentException("Inscrição estadual inválida");
            }

            int result = 0;
            int[] pesos = { 1, 3, 4, 5, 6, 7, 8, 10 };

            for (int i = 0; i < pesos.length; i++) {
                result += pesos[i] * Integer.parseInt(String.valueOf(ie.charAt(i + 1)));
            }

            result = (result % 11) == 10 ? 0 : (result % 11);

            if (!(result == Integer.parseInt(String.valueOf(ie.charAt(9))))) {
                throw new IllegalArgumentException("Inscrição estadual inválida");
            }

        } else {

            if (ie.length() != 12) {
                throw new IllegalArgumentException("Inscrição estadual inválida");
            }

            int result1 = 0, result2 = 0;

            int[] pesos1 = { 1, 3, 4, 5, 6, 7, 8, 10 }, pesos2 = { 3, 2, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

            for (int i = 0; i < pesos1.length; i++) {
                result1 += pesos1[i] * Integer.parseInt(String.valueOf(ie.charAt(i)));
            }

            result1 = (result1 % 11) == 10 ? 0 : (result1 % 11);

            for (int i = 0; i < pesos2.length; i++) {
                result2 += pesos2[i] * Integer.parseInt(String.valueOf(ie.charAt(i)));
            }

            result2 = (result2 % 11) == 10 ? 0 : (result2 % 11);

            if (!(result1 == Integer.parseInt(String.valueOf(ie.charAt(8)))
                    && result2 == Integer.parseInt(String.valueOf(ie.charAt(11))))) {
                throw new IllegalArgumentException("Inscrição estadual inválida");
            }
        }
        return ie;
    }

    /**
     * Função para validar o Email
     * 
     * Caso tudo ocorra certo, retornará o Email formatado para inserção.
     * Se houver algum erro será lançado uma excessão.
     * 
     * @param email
     * 
     * @return String email
     * 
     * @exception IllegalArgumentException
     */
    public static String validationEmail(String email) {
        if (email.isBlank() || !(emailPatternRegex(email))) {
            throw new IllegalArgumentException("Email inválido");
        }
        return email;
    }

    /**
     * Função interna para validar o Email utilizando expressões regulares.
     * 
     * Caso tudo ocorra certo, retornará true. Caso contrário retornará false.
     * 
     * @param email
     * 
     * @return boolean
     */
    private static boolean emailPatternRegex(String email) {
        return Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email)
                .matches();
    }

    /**
     * Função para verificar força da senha.
     * 
     * Caso tudo ocorra certo, retornará a senha.
     * Se houver algum error será lançado uma excessão.
     * 
     * @param password
     * 
     * @return String password
     * 
     * @exception ValidationException
     */
    public static String passwordStrength(String password) throws ValidationException {

        int points = 0;

        String specialChar = password.replaceAll("[a-zA-z0-9]", "");
        String upperCase = password.replaceAll("[^a-z]", "");
        String lowerCase = password.replaceAll("[^A-Z]", "");
        String number = password.replaceAll("[^0-9]", "");

        if (specialChar.length() == 0 || upperCase.length() == 0 || lowerCase.length() == 0 || number.length() == 0) {
            throw new ValidationException("A senha inserida é fraca!");
        }

        points = (specialChar.length() / specialChar.length()) + (upperCase.length() / upperCase.length())
                + (lowerCase.length() / lowerCase.length()) + (number.length() / number.length());

        if (!(points == 4)) {
            throw new ValidationException("A senha inserida é fraca!");
        }

        return password;
    }

    /**
     * Função para validar o RG.
     * 
     * Caso tudo ocorra certo, retornará o RG.
     * Se houver um algum erro será lançado uma excessão.
     * 
     * @param rg
     * 
     * @return String rg
     * 
     * @exception IllegalArgumentException
     */
    public static String validationRG(String rg) {

        rg = rg.replaceAll("[^0-9X]", "");

        if (rg.isBlank() || rg.length() != 9) {
            throw new IllegalArgumentException("RG inválido");
        }

        int result = 0;
        int[] pesos = { 2, 3, 4, 5, 6, 7, 8, 9 };

        for (int i = 0; i < pesos.length; i++) {
            result += pesos[i] * Integer.parseInt(String.valueOf(rg.charAt(i)));
        }

        result = (11 - (result % 11)) == 11 ? 0 : (11 - (result % 11));

        if (rg.contains("X")) {
            if (!(result == 10)) {
                throw new IllegalArgumentException("RG inválido");
            }

        } else {
            if (!(result == Integer.parseInt(String.valueOf(rg.charAt(8))))) {
                throw new IllegalArgumentException("RG inválido");
            }
        }

        return rg;
    }
}

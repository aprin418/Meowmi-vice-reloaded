package com.meowmivice.game.reader;

import com.meowmivice.game.controller.Game;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class FileReader {

    FileReader() throws IOException, ParseException {
    }

    public static void fileReader(String filename) throws IOException {
        try (var in = Game.class.getResourceAsStream(filename)) {
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
            while ( scanner.hasNextLine() ){
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException("Uncaught", e);
        }
    }

    // does the same as file reader but outputs the string in white color
    public static void fileReaderWhite(String filename) throws IOException {
        try (var in = Game.class.getResourceAsStream(filename)) {
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
            while ( scanner.hasNextLine() ){
                System.out.println("\033[37m" + scanner.nextLine()+ "\033[0m");
            }
        } catch (IOException e) {
            throw new RuntimeException("Uncaught", e);
        }
    }





}
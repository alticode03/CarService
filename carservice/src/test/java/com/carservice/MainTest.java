package com.carservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void displaysCrudMenusWithoutOpeningDatabaseAndExits() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputBytes);

        Main.run(new Scanner("0\n"), output);

        String text = outputBytes.toString();
        assertTrue(text.contains("CRUD pelanggan"));
        assertTrue(text.contains("CRUD kategori"));
        assertTrue(text.contains("CRUD produk/jasa"));
        assertTrue(text.contains("Selesai."));
    }

    @Test
    void reportsInvalidChoiceAndExitsFromTerminalMenu() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputBytes);

        Main.run(new Scanner("x\n0\n"), output);

        String text = outputBytes.toString();
        assertTrue(text.contains("Pilihan tidak dikenal."));
        assertTrue(text.contains("Selesai."));
    }
}

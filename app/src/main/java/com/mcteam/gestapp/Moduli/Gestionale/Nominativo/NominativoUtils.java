package com.mcteam.gestapp.Moduli.Gestionale.Nominativo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mcteam.gestapp.Models.Rubrica.Nominativo;
import com.mcteam.gestapp.Utils.HeaderFooterPageEvent;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Riccardo Rossi on 16/12/2015.
 */
public class NominativoUtils {

    public static Font tableTitleFond = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);

    public static void printSimple(Nominativo nominativo, Context context, boolean dettagliato) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/nominativo/pdf");
        Log.d("PATH DI STAMPA", context.getFilesDir() + "/GestApp/nominativo/pdf");
        File pdf = new File(dir, nominativo.getCognome() + ".pdf");

        dir.mkdirs();
        //make them in case they're not there

        //creazione tabella
        Font boldTitle = new Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD);
        Document pdfToPrint = new Document(PageSize.A4.rotate(), 20, 20, 100, 25);
        FileOutputStream fos = new FileOutputStream(pdf);
        PdfWriter writer = PdfWriter.getInstance(pdfToPrint, fos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent("STAMPA NOMINATIVO");
        event.setImage(context);

        writer.setPageEvent(event);
        pdfToPrint.open();

        tableTitleFond.setColor(BaseColor.WHITE);
        tableTitleFond.setSize(14);

        //adding meta data
        Paragraph titolo = new Paragraph(nominativo.getCognome() + " " + nominativo.getNome(), boldTitle);
        titolo.setAlignment(Element.ALIGN_CENTER);

        pdfToPrint.add(titolo);

        pdfToPrint.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);

        PdfPCell c1 = new PdfPCell(new Phrase("Titolo", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cognome", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Nome", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Società", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        table.addCell(nominativo.getTitolo() == null ? " " : nominativo.getTitolo().equals("") ? " " : nominativo.getTitolo());
        table.addCell(nominativo.getCognome() == null ? " " : nominativo.getCognome().equals("") ? " " : nominativo.getCognome());
        table.addCell(nominativo.getNote() == null ? " " : nominativo.getNote().equals("") ? " " : nominativo.getNote());
        if (nominativo.getSocieta() != null)
            table.addCell(nominativo.getSocieta().getNomeSocietà());

        pdfToPrint.add(table);

        table = new PdfPTable(4);
        c1 = new PdfPCell(new Phrase("Indirizzo", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("CAP", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Provincia", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Città", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        table.addCell(nominativo.getIndirizzo() == null ? " " : nominativo.getIndirizzo().equals("") ? " " : nominativo.getIndirizzo());
        table.addCell(nominativo.getCap() == null ? " " : nominativo.getCap().equals("") ? " " : nominativo.getCap());
        table.addCell(nominativo.getProvincia() == null ? " " : nominativo.getProvincia().equals("") ? " " : nominativo.getProvincia());
        table.addCell(nominativo.getCitta() == null ? " " : nominativo.getCitta().equals("") ? " " : nominativo.getCitta());

        pdfToPrint.add(table);

        table = new PdfPTable(4);
        c1 = new PdfPCell(new Phrase("Nazionalità", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Telefono", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Fax", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cellulare", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        table.addCell(nominativo.getNazionalita() == null ? " " : nominativo.getNazionalita().equals("") ? " " : nominativo.getNazionalita());
        table.addCell(nominativo.getTelefono() == null ? " " : nominativo.getTelefono().equals("") ? " " : nominativo.getTelefono());
        table.addCell(nominativo.getFax() == null ? " " : nominativo.getFax().equals("") ? " " : nominativo.getFax());
        table.addCell(nominativo.getCellulare() == null ? " " : nominativo.getCellulare().equals("") ? " " : nominativo.getCellulare());

        pdfToPrint.add(table);


        table = new PdfPTable(2);
        c1 = new PdfPCell(new Phrase("e-mail", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);


        c1 = new PdfPCell(new Phrase("Note", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        table.addCell(nominativo.getEmail() == null ? " " : nominativo.getEmail().equals("") ? " " : nominativo.getEmail());
        table.addCell(nominativo.getNote() == null ? " " : nominativo.getNote().equals("") ? " " : nominativo.getNote());

        pdfToPrint.add(table);

        if (dettagliato) {
            table = new PdfPTable(4);
            c1 = new PdfPCell(new Phrase("Data nascita", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Luogo nascita", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Partita IVA", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Codice fiscale", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            table.setHeaderRows(1);

            table.addCell(nominativo.getDataNascita() == null ? " " : nominativo.getDataNascita().equals("") ? " " : nominativo.getDataNascita());
            table.addCell(nominativo.getLuogoNascita() == null ? " " : nominativo.getLuogoNascita().equals("") ? " " : nominativo.getLuogoNascita());
            table.addCell(nominativo.getPIVA() == null ? " " : nominativo.getPIVA().equals("") ? " " : nominativo.getPIVA());
            table.addCell(nominativo.getCod_Fiscale() == null ? " " : nominativo.getCod_Fiscale().equals("") ? " " : nominativo.getCod_Fiscale());

            pdfToPrint.add(table);

            table = new PdfPTable(4);
            c1 = new PdfPCell(new Phrase("Carta d'identià", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Patente", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Tessera sanitaria", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Sito web", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            table.setHeaderRows(1);

            table.addCell(nominativo.getCartaID() == null ? " " : nominativo.getCartaID().equals("") ? " " : nominativo.getCartaID());
            table.addCell(nominativo.getPatente() == null ? " " : nominativo.getPatente().equals("") ? " " : nominativo.getPatente());
            table.addCell(nominativo.getTesseraSanitaria() == null ? " " : nominativo.getTesseraSanitaria().equals("") ? " " : nominativo.getTesseraSanitaria());
            table.addCell(nominativo.getSitoWeb() == null ? " " : nominativo.getSitoWeb().equals("") ? " " : nominativo.getSitoWeb());

            pdfToPrint.add(table);

            table = new PdfPTable(4);
            c1 = new PdfPCell(new Phrase("Nome banca", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Indirizzo banca", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("IBAN", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Note dettaglio", tableTitleFond));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.RED);
            table.addCell(c1);

            table.setHeaderRows(1);

            table.addCell(nominativo.getNomeBanca() == null ? " " : nominativo.getNomeBanca().equals("") ? " " : nominativo.getNomeBanca());
            table.addCell(nominativo.getIndirizzoBanca() == null ? " " : nominativo.getIndirizzoBanca().equals("") ? " " : nominativo.getIndirizzoBanca());
            table.addCell(nominativo.getIBAN() == null ? " " : nominativo.getIBAN().equals("") ? " " : nominativo.getIBAN());
            table.addCell(nominativo.getNoteDett() == null ? " " : nominativo.getNoteDett().equals("") ? " " : nominativo.getNoteDett());

            pdfToPrint.add(table);

        }
        //create a standard java.io.File object for the Workbook to use

        readPdfFile(Uri.fromFile(pdf), context, "pdf");

        pdfToPrint.close();

    }

    public static void readPdfFile(Uri path, Context context, String type) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (type.equals("pdf")) {
            intent.setDataAndType(path, "application/pdf");
        } else if (type.equals("excel")) {
            intent.setDataAndType(path, "application/vnd.ms-excel");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application Available to View this data", Toast.LENGTH_SHORT).show();
        }
    }


    public static void printAll(ArrayList<Nominativo> nominativi, Context context) throws DocumentException, FileNotFoundException {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/nominativiStampa");

        File pdf = new File(dir, "nominativi.pdf");

        //creazione tabella
        //make them in case they're not there
        dir.mkdirs();
        Font boldTitle = new Font(Font.FontFamily.HELVETICA, 15f, Font.BOLD);
        Document pdfToPrint = new Document(PageSize.A4.rotate(), 20, 20, 100, 25);
        FileOutputStream fos = new FileOutputStream(pdf);
        PdfWriter writer = PdfWriter.getInstance(pdfToPrint, fos);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent("STAMPA TUTTO NOMINATIVI");
        event.setImage(context);

        writer.setPageEvent(event);
        pdfToPrint.open();

        //adding meta data

        tableTitleFond.setColor(BaseColor.WHITE);
        tableTitleFond.setSize(15);

        PdfPTable table = new PdfPTable(13);

        PdfPCell c1 = new PdfPCell(new Phrase("T", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("COGNOME", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("NOME", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("SOCIETA'", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("INDIRIZZO", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("CAP", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("PV", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);


        c1 = new PdfPCell(new Phrase("CITTA'", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("TELEFONO", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("FAX", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("CELL", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("E-MAIL", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("NOTE", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        for (Nominativo nominativo : nominativi) {
            table.addCell(nominativo.getTitolo() == null ? " " : nominativo.getTitolo());
            table.addCell(nominativo.getCognome() == null ? " " : nominativo.getCognome());
            table.addCell(nominativo.getNome() == null ? " " : nominativo.getNome());
            if (nominativo.getSocieta() != null)
                table.addCell(nominativo.getSocieta().getNomeSocietà());
            else
                table.addCell(" ");
            table.addCell(nominativo.getIndirizzo() == null ? " " : nominativo.getIndirizzo());
            table.addCell(nominativo.getCap() == null ? " " : nominativo.getCap());
            table.addCell(nominativo.getProvincia() == null ? " " : nominativo.getProvincia());
            table.addCell(nominativo.getCitta() == null ? " " : nominativo.getCitta());
            table.addCell(nominativo.getTelefono() == null ? " " : nominativo.getTelefono());
            table.addCell(nominativo.getFax() == null ? " " : nominativo.getFax());
            table.addCell(nominativo.getCellulare() == null ? " " : nominativo.getCellulare());
            table.addCell(nominativo.getEmail() == null ? " " : nominativo.getEmail());
            table.addCell(nominativo.getNote() == null ? " " : nominativo.getNote());
        }

        pdfToPrint.add(table);

        //Write the workbook in file system
        readPdfFile(Uri.fromFile(pdf), context, "pdf");

        pdfToPrint.close();

    }

    public static void esportaExcel(ArrayList<Nominativo> listaNominativi, Context context) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/nominativi/excel");

        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, "Nominativi.xlsx");

        Workbook wb = new HSSFWorkbook();

        Cell cella = null;

        CellStyle cs = wb.createCellStyle();

        cs.setFillForegroundColor(HSSFColor.RED.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Sheet foglio1 = null;
        foglio1 = wb.createSheet("Nominativi");

        Row row = foglio1.createRow(0);

        cella = row.createCell(0);
        cella.setCellValue("Titolo");
        cella.setCellStyle(cs);

        cella = row.createCell(1);
        cella.setCellValue("Cognome");
        cella.setCellStyle(cs);

        cella = row.createCell(2);
        cella.setCellValue("Nome");
        cella.setCellStyle(cs);

        cella = row.createCell(3);
        cella.setCellValue("Società");
        cella.setCellStyle(cs);

        cella = row.createCell(4);
        cella.setCellValue("Città");
        cella.setCellStyle(cs);

        cella = row.createCell(5);
        cella.setCellValue("Telefono");
        cella.setCellStyle(cs);

        cella = row.createCell(6);
        cella.setCellValue("Fax");
        cella.setCellStyle(cs);

        cella = row.createCell(7);
        cella.setCellValue("Cellulare");
        cella.setCellStyle(cs);


        int nRow = 1;

        for (Nominativo nominativo : listaNominativi) {

            row = foglio1.createRow(nRow++);

            cella = row.createCell(0);
            cella.setCellValue(nominativo.getTitolo());

            cella = row.createCell(1);
            cella.setCellValue(nominativo.getCognome());

            cella = row.createCell(2);
            cella.setCellValue(nominativo.getNome());

            cella = row.createCell(3);
            if (nominativo.getSocieta() != null)
                cella.setCellValue(nominativo.getSocieta().getNomeSocietà());

            cella = row.createCell(4);
            cella.setCellValue(nominativo.getCitta());

            cella = row.createCell(5);
            cella.setCellValue(nominativo.getTelefono());

            cella = row.createCell(6);
            cella.setCellValue(nominativo.getFax());

            cella = row.createCell(7);
            cella.setCellValue(nominativo.getCellulare());

        }

        //Write the workbook in file system
        FileOutputStream out = null;
        System.out.println("xlsx written successfully");


        try {
            out = new FileOutputStream(wbfile);
            wb.write(out);
            readPdfFile(Uri.fromFile(wbfile), context, "excel");
            Log.w("FileUtils", "Writing file" + wbfile);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + wbfile, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != out)
                    out.close();
            } catch (Exception ex) {
            }
        }

    }


}

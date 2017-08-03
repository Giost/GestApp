package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mcteam.gestapp.Models.Commerciale.Offerta;
import com.mcteam.gestapp.Models.Commessa;
import com.mcteam.gestapp.Utils.Functions;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefano Gioda on 7/11/17.
 * Classe che raccoglie alcune funzioni utili come la stampa e l'esportazione in excell
 */
public class OfferteUtils
{
    //public static Font tableTitleFond = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);

    public static void readFile(Uri path, Context context, String type) {

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
            Toast.makeText(context, "No Application Available to View this type of file", Toast.LENGTH_SHORT).show();
        }
    }


    public static void printAll(ArrayList<Offerta> offerte, Commessa commessa, Context context) throws Exception {
        Font tableTitleFond = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);
        Font tableContentFont = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/offerte/pdf");

        File pdf = new File(dir, commessa.getNome_commessa() == null || commessa.getNome_commessa().equals("") ? " " : commessa.getNome_commessa()+".pdf");

        tableTitleFond.setColor(BaseColor.WHITE);

        tableTitleFond.setSize(9);

        //creazione tabella
        //make them in case they're not there
        dir.mkdirs();
        Font boldTitle = new Font(Font.FontFamily.HELVETICA, 15f, Font.BOLD);
        Document pdfToPrint = new Document(PageSize.A4.rotate(), 20, 20, 100, 25);


        PdfWriter writer = PdfWriter.getInstance(pdfToPrint, new FileOutputStream(pdf));

        HeaderFooterPageEvent event = new HeaderFooterPageEvent("STAMPA OFFERTE");

        event.setImage(context);

        writer.setPageEvent(event);


        pdfToPrint.open();

        //PdfPTable table = new PdfPTable(11);
        PdfPTable table = new PdfPTable(new float[]{12,33,30,35,30,20,30,30,30,20,18});
        table.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell(new Phrase("Codice", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Nome commessa", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cliente", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Referente commessa", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Consulente", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Versione", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Referente off. I", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Referente off. II", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Referente off. III", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Data offerta", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Presentata", tableTitleFond));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(BaseColor.RED);
        table.addCell(c1);

        table.setHeaderRows(1);

        tableContentFont.setColor(BaseColor.BLACK);
        tableContentFont.setSize(9);

        for (Offerta offerta : offerte) {
            table.addCell(new Phrase(commessa.getCodice_commessa() == null || commessa.getCodice_commessa().equals("") ? " " : commessa.getCodice_commessa(), tableContentFont));
            table.addCell(new Phrase(commessa.getNome_commessa() == null || commessa.getNome_commessa().equals("") ? " " : commessa.getNome_commessa(), tableContentFont));
            table.addCell(new Phrase(commessa.getCliente().getNomeSocietà() == null || commessa.getCliente().getNomeSocietà().equals("") ? " " : commessa.getCliente().getNomeSocietà(), tableContentFont));
            table.addCell(new Phrase(commessa.getReferente1() == null  ? " " : commessa.getReferente1().getCognome()+" "+commessa.getReferente1().getNome(), tableContentFont));
            table.addCell(new Phrase(commessa.getCommerciale() == null  ? " " : commessa.getCommerciale().getCognome()+" "+commessa.getCommerciale().getNome(), tableContentFont));
            table.addCell(new Phrase("v_"+String.valueOf(offerta.getVersione()), tableContentFont));
            table.addCell(new Phrase(commessa.getReferente_offerta1() == null  ? " " : commessa.getReferente_offerta1().getCognome()+" "+commessa.getReferente_offerta1().getNome(), tableContentFont));
            table.addCell(new Phrase(commessa.getReferente_offerta2() == null  ? " " : commessa.getReferente_offerta2().getCognome()+" "+commessa.getReferente_offerta2().getNome(), tableContentFont));
            table.addCell(new Phrase(commessa.getReferente_offerta3() == null  ? " " : commessa.getReferente_offerta3().getCognome()+" "+commessa.getReferente_offerta3().getNome(), tableContentFont));
            table.addCell(new Phrase(Functions.getFormattedDate(offerta.getDataOfferta()), tableContentFont));
            table.addCell(new Phrase(offerta.getAccettata() == 0 ? "NO" : "SI", tableContentFont));

            /*table.addCell(banca.getNome() == null || banca.getNome().equals("") ? " " : banca.getNome());
            table.addCell(banca.getIndirizzo() == null || banca.getIndirizzo().equals("") ? " " : banca.getIndirizzo());
            table.addCell(banca.getIban() == null || banca.getIban().equals("") ? " " : banca.getIban());
            table.addCell(banca.getReferente() == null || banca.getReferente().equals("") ? " " : banca.getReferente());*/

        }
        pdfToPrint.add(table);

        //create a standard java.io.File object for the Workbook to use

        readFile(Uri.fromFile(pdf), context, "pdf");

        pdfToPrint.close();
    }

    public static void esportaExcel(ArrayList<Offerta> offerte, Commessa commessa, Context context) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/offerte/excel");

        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir, commessa.getNome_commessa() == null || commessa.getNome_commessa().equals("") ? " " : commessa.getNome_commessa()+".xlsx");

        Workbook wb = new HSSFWorkbook();

        Cell cella = null;

        CellStyle cs = wb.createCellStyle();

        cs.setFillForegroundColor(HSSFColor.RED.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        Sheet foglio1 = null;
        foglio1 = wb.createSheet("Offerte");

        Row row = foglio1.createRow(0);

        cella = row.createCell(0);
        cella.setCellValue("Codice");
        cella.setCellStyle(cs);

        cella = row.createCell(1);
        cella.setCellValue("Nome commessa");
        cella.setCellStyle(cs);

        cella = row.createCell(2);
        cella.setCellValue("Cliente");
        cella.setCellStyle(cs);

        cella = row.createCell(3);
        cella.setCellValue("Referente commessa");
        cella.setCellStyle(cs);

        cella = row.createCell(4);
        cella.setCellValue("Consulente");
        cella.setCellStyle(cs);

        cella = row.createCell(5);
        cella.setCellValue("Versione");
        cella.setCellStyle(cs);

        cella = row.createCell(6);
        cella.setCellValue("Referente off. I");
        cella.setCellStyle(cs);

        cella = row.createCell(7);
        cella.setCellValue("Referente off. II");
        cella.setCellStyle(cs);

        cella = row.createCell(8);
        cella.setCellValue("Referente off. III");
        cella.setCellStyle(cs);

        cella = row.createCell(9);
        cella.setCellValue("Data offerta");
        cella.setCellStyle(cs);

        cella = row.createCell(10);
        cella.setCellValue("Presentata");
        cella.setCellStyle(cs);

        cella = row.createCell(11);
        cella.setCellValue("Allegato");
        cella.setCellStyle(cs);

        int nRow = 1;

        for (Offerta offerta : offerte) {

            row = foglio1.createRow(nRow++);

            cella = row.createCell(0);
            cella.setCellValue(commessa.getCodice_commessa() == null || commessa.getCodice_commessa().equals("") ? " " : commessa.getCodice_commessa());

            cella = row.createCell(1);
            cella.setCellValue(commessa.getNome_commessa() == null || commessa.getNome_commessa().equals("") ? " " : commessa.getNome_commessa());

            cella = row.createCell(2);
            cella.setCellValue(commessa.getCliente().getNomeSocietà() == null || commessa.getCliente().getNomeSocietà().equals("") ? " " : commessa.getCliente().getNomeSocietà());

            cella = row.createCell(3);
            cella.setCellValue(commessa.getReferente1() == null  ? " " : commessa.getReferente1().getCognome()+" "+commessa.getReferente1().getNome());

            cella = row.createCell(4);
            cella.setCellValue(commessa.getCommerciale() == null  ? " " : commessa.getCommerciale().getCognome()+" "+commessa.getCommerciale().getNome());

            cella = row.createCell(5);
            cella.setCellValue("v_"+String.valueOf(offerta.getVersione()));

            cella = row.createCell(6);
            cella.setCellValue(commessa.getReferente_offerta1() == null  ? " " : commessa.getReferente_offerta1().getCognome()+" "+commessa.getReferente_offerta1().getNome());

            cella = row.createCell(7);
            cella.setCellValue(commessa.getReferente_offerta2() == null  ? " " : commessa.getReferente_offerta2().getCognome()+" "+commessa.getReferente_offerta2().getNome());

            cella = row.createCell(8);
            cella.setCellValue(commessa.getReferente_offerta3() == null  ? " " : commessa.getReferente_offerta3().getCognome()+" "+commessa.getReferente_offerta3().getNome());

            cella = row.createCell(9);
            cella.setCellValue(Functions.getFormattedDate(offerta.getDataOfferta()));

            cella = row.createCell(10);
            cella.setCellValue(String.valueOf(offerta.getAccettata()));

            cella = row.createCell(11);
            cella.setCellValue(offerta.getAllegato());

        }
        /*################ LARGHEZZE DA SETTARE #################*/
        /*foglio1.setColumnWidth(0, 5000);
        foglio1.setColumnWidth(1, 2000);
        foglio1.setColumnWidth(2, 10000);
        foglio1.setColumnWidth(3, 5000);
        foglio1.setColumnWidth(4, 5000);
        foglio1.setColumnWidth(5, 2000);
        foglio1.setColumnWidth(6, 2000);*/
        //Write the workbook in file system
        FileOutputStream out = null;


        try {
            out = new FileOutputStream(wbfile);
            wb.write(out);
            readFile(Uri.fromFile(wbfile), context, "excel");
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

package com.mcteam.gestapp.Moduli.Commerciale.Offerte;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Stefano Gioda on 7/11/17.
 * Classe che raccoglie alcune funzioni utili come la stampa e l'esportazione in excell
 */
public class OfferteUtils
{
    public static Font tableTitleFond = new Font(Font.FontFamily.UNDEFINED, Font.BOLD);

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
            Toast.makeText(context, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
        }
    }


    public static void printAll(ArrayList<Offerta> offerte, Commessa commessa, Context context) throws Exception {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/GestApp/offerte/pdf");

        File pdf = new File(dir, "stampa_offerte.pdf");

        tableTitleFond.setColor(BaseColor.WHITE);

        tableTitleFond.setSize(14);

        //creazione tabella
        //make them in case they're not there
        dir.mkdirs();
        Font boldTitle = new Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD);
        Document pdfToPrint = new Document(PageSize.A4.rotate(), 20, 20, 100, 25);


        PdfWriter writer = PdfWriter.getInstance(pdfToPrint, new FileOutputStream(pdf));

        HeaderFooterPageEvent event = new HeaderFooterPageEvent("STAMPA OFFERTE");

        event.setImage(context);

        writer.setPageEvent(event);


        pdfToPrint.open();

        PdfPTable table = new PdfPTable(11);

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

        for (Offerta offerta : offerte) {
            table.addCell(commessa.getCodice_commessa() == null || commessa.getCodice_commessa().equals("") ? " " : commessa.getCodice_commessa());
            table.addCell(commessa.getNome_commessa() == null || commessa.getNome_commessa().equals("") ? " " : commessa.getNome_commessa());
            table.addCell(commessa.getCliente().getNomeSocietà() == null || commessa.getCliente().getNomeSocietà().equals("") ? " " : commessa.getCliente().getNomeSocietà());
            table.addCell(commessa.getReferente1() == null  ? " " : commessa.getReferente1().getCognome()+" "+commessa.getReferente1().getNome());
            table.addCell(commessa.getCommerciale() == null  ? " " : commessa.getCommerciale().getCognome()+" "+commessa.getCommerciale().getNome());
            table.addCell("v_"+String.valueOf(offerta.getVersione()));
            table.addCell(commessa.getReferente_offerta1() == null  ? " " : commessa.getReferente_offerta1().getCognome()+" "+commessa.getReferente_offerta1().getNome());
            table.addCell(commessa.getReferente_offerta2() == null  ? " " : commessa.getReferente_offerta2().getCognome()+" "+commessa.getReferente_offerta2().getNome());
            table.addCell(commessa.getReferente_offerta3() == null  ? " " : commessa.getReferente_offerta3().getCognome()+" "+commessa.getReferente_offerta3().getNome());
            table.addCell(Functions.getFormattedDate(offerta.getDataOfferta()));
            table.addCell(String.valueOf(offerta.getAccettata()));

            /*table.addCell(banca.getNome() == null || banca.getNome().equals("") ? " " : banca.getNome());
            table.addCell(banca.getIndirizzo() == null || banca.getIndirizzo().equals("") ? " " : banca.getIndirizzo());
            table.addCell(banca.getIban() == null || banca.getIban().equals("") ? " " : banca.getIban());
            table.addCell(banca.getReferente() == null || banca.getReferente().equals("") ? " " : banca.getReferente());*/

        }
        pdfToPrint.add(table);

        //create a standard java.io.File object for the Workbook to use

        readPdfFile(Uri.fromFile(pdf), context, "pdf");

        pdfToPrint.close();
    }
}

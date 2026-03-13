package com.tournamentmanager.util;

import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Tournament;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static void export(List<Match> matches, Tournament tournament) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Titre
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                content.newLineAtOffset(50, 780);
                content.showText("Bracket - " + tournament.getName());
                content.endText();

                // En-têtes du tableau
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                content.newLineAtOffset(50, 740);
                content.showText("Round");
                content.newLineAtOffset(80, 0);
                content.showText("Joueur 1");
                content.newLineAtOffset(150, 0);
                content.showText("Joueur 2");
                content.newLineAtOffset(150, 0);
                content.showText("Gagnant");
                content.endText();

                // Ligne séparatrice
                content.moveTo(50, 730);
                content.lineTo(550, 730);
                content.stroke();

                // Lignes des matchs
                float y = 710;
                for (Match m : matches) {
                    content.beginText();
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                    content.newLineAtOffset(50, y);
                    content.showText(String.valueOf(m.getRound()));
                    content.newLineAtOffset(80, 0);
                    content.showText(m.getPlayer1().getName());
                    content.newLineAtOffset(150, 0);
                    content.showText(m.getPlayer2().getName());
                    content.newLineAtOffset(150, 0);
                    content.showText(m.getWinner() != null ? m.getWinner().getName() : "À jouer");
                    content.endText();
                    y -= 25;
                }
            }

            // Sauvegarde
            String filename = "bracket_" + tournament.getName() + ".pdf";
            document.save(filename);
            System.out.println("PDF exporté : " + filename);

        } catch (IOException e) {
            System.out.println("Erreur PDF : " + e.getMessage());
        }
    }
}
package com.tournamentmanager.util;

import com.tournamentmanager.model.Match;
import com.tournamentmanager.model.Player;
import com.tournamentmanager.model.Tournament;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PdfExporter {

    // Palette de l'app (RGB 0-1)
    private static final float[] BLUE = {52f / 255f, 152f / 255f, 219f / 255f};    // #3498db
    private static final float[] DARK_BLUE = {44f / 255f, 62f / 255f, 80f / 255f}; // #2c3e50
    private static final float[] WHITE = {1f, 1f, 1f};
    private static final float[] GOLD = {241f / 255f, 196f / 255f, 15f / 255f};    // #f1c40f

    private static void fill(PDPageContentStream c, float[] rgb) throws IOException {
        c.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
    }

    private static void stroke(PDPageContentStream c, float[] rgb) throws IOException {
        c.setStrokingColor(rgb[0], rgb[1], rgb[2]);
    }

    // Layout
    private static final float MARGIN = 40f;
    private static final float TITLE_ZONE = 60f;
    private static final float ROUND_HEADER_ZONE = 25f;
    private static final float BOX_WIDTH = 130f;
    private static final float CELL_HEIGHT = 18f;
    private static final float BOX_HEIGHT = CELL_HEIGHT * 2;

    public static void export(List<Match> matches, Tournament tournament) {
        if (matches == null || matches.isEmpty()) {
            System.out.println("Pas de matchs à exporter.");
            return;
        }

        try (PDDocument document = new PDDocument()) {
            // A4 paysage
            PDRectangle a4 = PDRectangle.A4;
            PDPage page = new PDPage(new PDRectangle(a4.getHeight(), a4.getWidth()));
            document.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawTitle(content, fontBold, fontRegular, tournament, pageWidth, pageHeight);
                drawBracket(content, fontBold, fontRegular, matches, pageWidth, pageHeight);
            }

            String filename = "bracket_" + tournament.getName() + ".pdf";
            document.save(filename);
            System.out.println("PDF exporté : " + filename);

        } catch (IOException e) {
            System.out.println("Erreur PDF : " + e.getMessage());
        }
    }

    private static void drawTitle(PDPageContentStream content, PDFont fontBold, PDFont fontRegular,
                                  Tournament t, float pageWidth, float pageHeight) throws IOException {
        // Titre principal
        String title = "Bracket - " + t.getName();
        float titleSize = 20;
        float titleWidth = fontBold.getStringWidth(title) / 1000f * titleSize;
        float titleX = (pageWidth - titleWidth) / 2f;
        float titleY = pageHeight - MARGIN - titleSize;

        fill(content, DARK_BLUE);
        content.beginText();
        content.setFont(fontBold, titleSize);
        content.newLineAtOffset(titleX, titleY);
        content.showText(title);
        content.endText();

        // Sous-titre : jeu · date
        String subtitle = buildSubtitle(t);
        if (!subtitle.isEmpty()) {
            float subSize = 11;
            float subWidth = fontRegular.getStringWidth(subtitle) / 1000f * subSize;
            float subX = (pageWidth - subWidth) / 2f;
            float subY = titleY - subSize - 4;

            fill(content, BLUE);
            content.beginText();
            content.setFont(fontRegular, subSize);
            content.newLineAtOffset(subX, subY);
            content.showText(subtitle);
            content.endText();
        }

        // Ligne bleue sous l'ensemble titre + sous-titre
        float lineY = titleY - (subtitle.isEmpty() ? 8 : 22);
        stroke(content, BLUE);
        content.setLineWidth(1.5f);
        content.moveTo(MARGIN, lineY);
        content.lineTo(pageWidth - MARGIN, lineY);
        content.stroke();
    }

    private static String buildSubtitle(Tournament t) {
        StringBuilder sb = new StringBuilder();
        if (t.getGame() != null && !t.getGame().isBlank()) {
            sb.append(t.getGame());
        }
        if (t.getDate() != null && !t.getDate().isBlank()) {
            if (sb.length() > 0) sb.append("  \u00B7  ");
            sb.append(t.getDate());
        }
        return sb.toString();
    }

    private static void drawBracket(PDPageContentStream content, PDFont fontBold, PDFont fontRegular,
                                    List<Match> matches, float pageWidth, float pageHeight) throws IOException {
        // Regroupement par round
        Map<Integer, List<Match>> byRound = new TreeMap<>();
        for (Match m : matches) {
            byRound.computeIfAbsent(m.getRound(), k -> new ArrayList<>()).add(m);
        }
        byRound.values().forEach(list -> list.sort(Comparator.comparingInt(Match::getId)));

        List<Integer> roundKeys = new ArrayList<>(byRound.keySet());
        int totalRounds = roundKeys.size();

        // Zone disponible
        float bracketTop = pageHeight - MARGIN - TITLE_ZONE - ROUND_HEADER_ZONE;
        float bracketBottom = MARGIN;
        float usableHeight = bracketTop - bracketBottom;
        float usableWidth = pageWidth - 2 * MARGIN;
        float columnWidth = usableWidth / totalRounds;

        // Placement top-down : on part du dernier round (finale) centré,
        // puis on place chaque match des rounds précédents autour de son "enfant".
        Map<Match, Float> centerY = new IdentityHashMap<>();
        List<Match> lastRound = byRound.get(roundKeys.get(totalRounds - 1));
        if (lastRound.size() == 1) {
            centerY.put(lastRound.get(0), bracketBottom + usableHeight / 2f);
        } else {
            float lastGap = usableHeight / (lastRound.size() + 1);
            for (int i = 0; i < lastRound.size(); i++) {
                centerY.put(lastRound.get(i), bracketTop - lastGap * (i + 1));
            }
        }

        // Rounds précédents : offset = usableHeight / 2^(totalRounds - r)
        List<Match> orphans = new ArrayList<>();
        for (int r = totalRounds - 2; r >= 0; r--) {
            List<Match> curr = byRound.get(roundKeys.get(r));
            List<Match> next = byRound.get(roundKeys.get(r + 1));
            float offset = usableHeight / (float) Math.pow(2, totalRounds - r);
            for (int i = 0; i < curr.size(); i++) {
                int childIdx = i / 2;
                if (childIdx < next.size()) {
                    float childY = centerY.get(next.get(childIdx));
                    float y = (i % 2 == 0) ? childY + offset : childY - offset;
                    centerY.put(curr.get(i), y);
                } else {
                    orphans.add(curr.get(i));
                }
            }
        }

        // Orphelins (matchs sans successeur, cas non puissance de 2) empilés en bas
        float orphanY = bracketBottom + BOX_HEIGHT / 2f + 5;
        for (Match m : orphans) {
            centerY.put(m, orphanY);
            orphanY += BOX_HEIGHT + 6;
        }

        // Entêtes de round
        for (int r = 0; r < totalRounds; r++) {
            float cx = MARGIN + r * columnWidth + columnWidth / 2f;
            String label = roundLabel(byRound.get(roundKeys.get(r)).size(), r);
            float labelSize = 11;
            float textWidth = fontBold.getStringWidth(label) / 1000f * labelSize;
            fill(content, DARK_BLUE);
            content.beginText();
            content.setFont(fontBold, labelSize);
            content.newLineAtOffset(cx - textWidth / 2f, bracketTop + 8);
            content.showText(label);
            content.endText();
        }

        // Boîtes des matchs
        for (int r = 0; r < totalRounds; r++) {
            List<Match> roundMatches = byRound.get(roundKeys.get(r));
            float boxLeft = MARGIN + r * columnWidth + (columnWidth - BOX_WIDTH) / 2f;
            for (Match m : roundMatches) {
                float cy = centerY.get(m);
                drawMatchBox(content, fontBold, fontRegular, m, boxLeft, cy);
            }
        }

        // Lignes de connexion entre rounds
        stroke(content, BLUE);
        content.setLineWidth(1.2f);
        for (int r = 0; r < totalRounds - 1; r++) {
            List<Match> curr = byRound.get(roundKeys.get(r));
            List<Match> next = byRound.get(roundKeys.get(r + 1));
            float currBoxRight = MARGIN + r * columnWidth + (columnWidth - BOX_WIDTH) / 2f + BOX_WIDTH;
            float nextBoxLeft = MARGIN + (r + 1) * columnWidth + (columnWidth - BOX_WIDTH) / 2f;
            float midX = (currBoxRight + nextBoxLeft) / 2f;

            for (int i = 0; i < next.size(); i++) {
                Match m1 = curr.get(2 * i);
                Match m2 = curr.get(2 * i + 1);
                Match nextMatch = next.get(i);
                float y1 = centerY.get(m1);
                float y2 = centerY.get(m2);
                float yMid = centerY.get(nextMatch);

                content.moveTo(currBoxRight, y1);
                content.lineTo(midX, y1);
                content.moveTo(currBoxRight, y2);
                content.lineTo(midX, y2);
                content.moveTo(midX, y1);
                content.lineTo(midX, y2);
                content.moveTo(midX, yMid);
                content.lineTo(nextBoxLeft, yMid);
            }
        }
        content.stroke();

        // Petit badge étoile à côté du nom du gagnant de la finale
        if (!lastRound.isEmpty() && lastRound.get(0).getWinner() != null) {
            Match finalMatch = lastRound.get(0);
            Player winner = finalMatch.getWinner();
            Player p1 = finalMatch.getPlayer1();
            boolean winnerIsP1 = p1 != null && winner.getId() == p1.getId();
            float finalCy = centerY.get(finalMatch);
            float finalBoxLeft = MARGIN + (totalRounds - 1) * columnWidth + (columnWidth - BOX_WIDTH) / 2f;
            float winnerCellCenterY = winnerIsP1
                    ? finalCy + CELL_HEIGHT / 2f
                    : finalCy - CELL_HEIGHT / 2f;
            float starCx = finalBoxLeft + BOX_WIDTH - 9f;
            drawStar(content, starCx, winnerCellCenterY, 5f);
        }
    }

    private static void drawStar(PDPageContentStream c, float cx, float cy, float r) throws IOException {
        double[] xs = new double[10];
        double[] ys = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            double radius = (i % 2 == 0) ? r : r * 0.42;
            xs[i] = cx + radius * Math.cos(angle);
            ys[i] = cy + radius * Math.sin(angle);
        }
        fill(c, GOLD);
        c.moveTo((float) xs[0], (float) ys[0]);
        for (int i = 1; i < 10; i++) {
            c.lineTo((float) xs[i], (float) ys[i]);
        }
        c.closePath();
        c.fill();
    }

    private static void drawMatchBox(PDPageContentStream content, PDFont fontBold, PDFont fontRegular,
                                     Match m, float x, float cy) throws IOException {
        float topCellBottom = cy;
        float botCellBottom = cy - CELL_HEIGHT;

        Player w = m.getWinner();
        Player p1 = m.getPlayer1();
        Player p2 = m.getPlayer2();
        boolean p1Winner = w != null && p1 != null && w.getId() == p1.getId();
        boolean p2Winner = w != null && p2 != null && w.getId() == p2.getId();

        drawCell(content, fontBold, fontRegular, x, topCellBottom, BOX_WIDTH, CELL_HEIGHT,
                p1 != null ? p1.getName() : "-", p1Winner);
        drawCell(content, fontBold, fontRegular, x, botCellBottom, BOX_WIDTH, CELL_HEIGHT,
                p2 != null ? p2.getName() : "-", p2Winner);
    }

    private static void drawCell(PDPageContentStream content, PDFont fontBold, PDFont fontRegular,
                                 float x, float bottomY, float w, float h, String text,
                                 boolean isWinner) throws IOException {
        // Fond
        fill(content, isWinner ? BLUE : WHITE);
        content.addRect(x, bottomY, w, h);
        content.fill();

        // Bordure
        stroke(content, BLUE);
        content.setLineWidth(0.8f);
        content.addRect(x, bottomY, w, h);
        content.stroke();

        // Texte
        fill(content, isWinner ? WHITE : DARK_BLUE);
        PDFont font = isWinner ? fontBold : fontRegular;
        float fontSize = 9f;
        String displayed = truncate(text != null ? text : "", font, fontSize, w - 10);

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x + 5, bottomY + h / 2f - fontSize / 3f);
        content.showText(displayed);
        content.endText();
    }

    private static String truncate(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        float width = font.getStringWidth(text) / 1000f * fontSize;
        if (width <= maxWidth) return text;
        String ellipsis = "...";
        String current = text;
        while (current.length() > 0) {
            current = current.substring(0, current.length() - 1);
            float w = font.getStringWidth(current + ellipsis) / 1000f * fontSize;
            if (w <= maxWidth) return current + ellipsis;
        }
        return ellipsis;
    }

    private static String roundLabel(int matchCount, int roundIndex) {
        switch (matchCount) {
            case 1: return "Finale";
            case 2: return "Demi-finales";
            case 4: return "Quarts de finale";
            case 8: return "Huitièmes de finale";
            case 16: return "Seizièmes de finale";
            default: return "Round " + (roundIndex + 1);
        }
    }
}

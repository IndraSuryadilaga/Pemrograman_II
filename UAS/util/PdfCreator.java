package util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import model.Match;
import model.Player;
import java.io.FileNotFoundException;
import java.util.List;

public class PdfCreator {

    public static void generateMatchReport(Match match, List<Player> homePlayers, List<Player> awayPlayers, String dest) throws FileNotFoundException {
        // 1. Inisialisasi PDF Writer & Document (Style iText 7)
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // 2. HEADER
        Paragraph title = new Paragraph("LAPORAN HASIL PERTANDINGAN")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        
        // 3. INFO TURNAMEN
        String roundName = (match.getRoundNumber() == 1) ? "Final" : (match.getRoundNumber() == 2) ? "Semi Final" : "Round " + match.getRoundNumber();
        Paragraph info = new Paragraph("Turnamen: " + match.getTournamentName() + "\n" +
                                       "Babak: " + roundName + "\n" +
                                       "Waktu: " + match.getMatchDate())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(info);

        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------"));

        // 4. SKOR BIG MATCH
        String scoreText = match.getHomeTeamName() + "  " + match.getHomeScore() + "  -  " + match.getAwayScore() + "  " + match.getAwayTeamName();
        Paragraph scorePara = new Paragraph(scoreText)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE);
        document.add(scorePara);
        
        String winner = (match.getHomeScore() > match.getAwayScore()) ? match.getHomeTeamName() : match.getAwayTeamName();
        if(match.getHomeScore() == match.getAwayScore()) winner = "SERI";
        
        Paragraph winPara = new Paragraph("Pemenang: " + winner)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(winPara);

        // 5. TABEL STATISTIK
        document.add(createTeamTable(match.getHomeTeamName(), homePlayers));
        document.add(new Paragraph("\n")); // Spasi antar tabel
        document.add(createTeamTable(match.getAwayTeamName(), awayPlayers));

        // 6. FOOTER
        Paragraph footer = new Paragraph("\nDicetak otomatis oleh Sistem SPORTA")
                .setItalic()
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(footer);

        document.close();
    }

    // Helper untuk membuat Tabel Statistik Tim
    private static Table createTeamTable(String teamName, List<Player> players) {
        // Membuat tabel dengan lebar kolom proporsional (No, Nama, Poin, Foul)
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Header Judul Tim
        Cell headerTeam = new Cell(1, 4)
                .add(new Paragraph("Statistik: " + teamName))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        table.addHeaderCell(headerTeam);

        // Header Kolom
        table.addHeaderCell(new Cell().add(new Paragraph("#")).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Cell().add(new Paragraph("Nama Pemain")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Poin")).setBold().setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Cell().add(new Paragraph("Foul")).setBold().setTextAlignment(TextAlignment.CENTER));

        // Isi Data
        for (Player p : players) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getJerseyNumber()))).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(p.getName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMatchPoints()))).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMatchFouls()))).setTextAlignment(TextAlignment.CENTER));
        }
        return table;
    }
}
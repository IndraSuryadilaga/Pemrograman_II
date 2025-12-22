package helper;

import model.Match;
import model.Player;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;
import java.util.List;

// Utility class untuk men-generate laporan pertandingan dalam format PDF menggunakan library iText
public class PdfCreator {

    public static void generateMatchReport(Match match, List<Player> homePlayers, List<Player> awayPlayers, String dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // HAPUS .setTextAlignment(...)
        Paragraph title = new Paragraph("LAPORAN HASIL PERTANDINGAN")
                .setFontSize(18)
                .setBold();
        document.add(title);

        document.add(new Paragraph("Turnamen: " + match.getTournamentName()));
        document.add(new Paragraph("Tanggal: " + match.getMatchDate()));
        
        // HAPUS .setTextAlignment(...)
        Paragraph scorePara = new Paragraph("Skor Akhir: " + match.getHomeTeamName() + " (" + match.getHomeScore() + ") vs (" + match.getAwayScore() + ") " + match.getAwayTeamName())
                .setBold()
                .setFontSize(14)
                .setMarginBottom(10);
        document.add(scorePara);

        document.add(createTeamTable(match.getHomeTeamName(), homePlayers));
        document.add(new Paragraph("\n")); 

        document.add(createTeamTable(match.getAwayTeamName(), awayPlayers));

        document.close();
        System.out.println("PDF berhasil dibuat di: " + dest);
    }

    private static Table createTeamTable(String teamName, List<Player> players) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // HAPUS .setTextAlignment(...)
        Cell headerTeam = new Cell(1, 4)
                .add(new Paragraph("Statistik: " + teamName))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold();
        table.addHeaderCell(headerTeam);

        // HAPUS .setTextAlignment(...)
        table.addHeaderCell(new Cell().add(new Paragraph("#")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Nama Pemain")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Poin")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Foul")).setBold());

        for (Player p : players) {
            // HAPUS .setTextAlignment(...)
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getJerseyNumber()))));
            table.addCell(new Cell().add(new Paragraph(p.getName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMatchPoints()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMatchFouls()))));
        }

        return table;
    }
}
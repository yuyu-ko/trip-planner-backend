package com.example.tripplanner.service;

import com.example.tripplanner.model.Activity;
import com.example.tripplanner.model.DayPlan;
import com.example.tripplanner.model.Trip;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PDFService {

    // 🎨 定義設計顏色
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80); // Deep Navy (深藍色)
    private static final Color ACCENT_COLOR = new Color(52, 152, 219); // Bright Blue (點綴藍)
    private static final Color TEXT_DARK = new Color(52, 73, 94); // Dark Text
    private static final Color TEXT_MILD = new Color(127, 140, 141); // Light Gray Text
    private static final Color LINE_COLOR = new Color(236, 240, 241); // Very Light Gray Line

    // 📌 字體定義
    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 36, Font.BOLD, PRIMARY_COLOR);
    private static final Font FONT_SUBTITLE = new Font(Font.HELVETICA, 18, Font.NORMAL, TEXT_MILD);
    private static final Font FONT_HEADER_DAY = new Font(Font.HELVETICA, 22, Font.BOLD, PRIMARY_COLOR);
    private static final Font FONT_LABEL = new Font(Font.HELVETICA, 12, Font.BOLD, TEXT_DARK);
    private static final Font FONT_TEXT = new Font(Font.HELVETICA, 12, Font.NORMAL, TEXT_DARK);

    // 活動卡片字體
    private static final Font FONT_TIME = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
    private static final Font FONT_ACTIVITY_TITLE = new Font(Font.HELVETICA, 14, Font.BOLD, TEXT_DARK);
    private static final Font FONT_ACTIVITY_DETAIL = new Font(Font.HELVETICA, 11, Font.NORMAL, TEXT_MILD);
    private static final Font FONT_DESCRIPTION = new Font(Font.HELVETICA, 10, Font.ITALIC, TEXT_MILD);


    public byte[] generateTripPDF(Trip trip) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 調整邊距，使內容更寬敞
        Document doc = new Document(PageSize.A4, 60, 60, 60, 60);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // ====== 🎨 封面 標題 ======

        // 主標題
        Paragraph title = new Paragraph("ITINERARY REPORT", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        // 副標題（城市）
        Paragraph subtitle = new Paragraph(trip.getCity().toUpperCase(), FONT_SUBTITLE);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(40);
        doc.add(subtitle);

        // -------------------------

        // ====== 📅 基本資訊區塊 (使用 Table 結構化排版) ======

        // 創建一個 2 欄表格來對齊標籤和值
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(80); // 資訊欄位稍微寬一點
        infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{30, 70});

        addInfoRow(infoTable, "Date:", trip.getStartDate() + " → " + trip.getEndDate());
        addInfoRow(infoTable, "Budget Level:", trip.getBudgetLevel().name());
        addInfoRow(infoTable, "Preference:", String.join(", ", trip.getPreferences()));

        doc.add(infoTable);

        // ====== 🗂 行程內容 ======

        for (DayPlan day : trip.getDayPlans()) {

            // Day Header - 醒目的橫條
            Paragraph dayHeader = new Paragraph(
                    "Day " + day.getDayNumber() + ": " + day.getDate(),
                    FONT_HEADER_DAY
            );
            dayHeader.setSpacingBefore(10);
            dayHeader.setSpacingAfter(15);

            // 底部線條 (使用 Chunk 模擬)
            Chunk line = new Chunk("================================================================================", new Font(Font.HELVETICA, 10, Font.NORMAL, ACCENT_COLOR));
            Paragraph linePara = new Paragraph(line);
            linePara.setSpacingAfter(20);

            doc.add(dayHeader);
            doc.add(linePara);

            // ====== 活動卡片 (兩欄佈局) ======

            for (Activity a : day.getActivities()) {

                PdfPTable activityCard = new PdfPTable(2);
                activityCard.setWidthPercentage(100);
                activityCard.setWidths(new float[]{20, 80}); // 左邊欄位給時間，右邊給內容
                activityCard.setSpacingAfter(15); // 每個活動之間間距

                // 1. 左側：時間標籤 (使用實心色塊)
                PdfPCell timeCell = new PdfPCell();
                timeCell.setBackgroundColor(PRIMARY_COLOR);
                timeCell.setBorder(Rectangle.NO_BORDER);
                timeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                timeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                timeCell.setPadding(10);

                // 嵌套一個段落，讓文字能換行
                Paragraph timePara = new Paragraph(a.getTime(), FONT_TIME);
                timePara.setAlignment(Element.ALIGN_CENTER);
                timeCell.addElement(timePara);
                activityCard.addCell(timeCell);

                // 2. 右側：活動內容
                PdfPCell contentCell = new PdfPCell();
                contentCell.setBorder(Rectangle.NO_BORDER);
                contentCell.setPadding(10);

                // Title
                Paragraph titlePara = new Paragraph(a.getTitle().toUpperCase(), FONT_ACTIVITY_TITLE);
                titlePara.setSpacingAfter(3);
                contentCell.addElement(titlePara);

                // Location & Rating
                String ratingText = (a.getGoogleRating() != null)
                        ? "⭐ " + String.format("%.1f", a.getGoogleRating())
                        : "Rating: -";

                Paragraph detailPara = new Paragraph(
                        a.getLocation() + " | " + ratingText, FONT_ACTIVITY_DETAIL
                );
                detailPara.setSpacingAfter(5);
                contentCell.addElement(detailPara);

                // Description
                contentCell.addElement(new Paragraph(a.getDescription(), FONT_DESCRIPTION));

                // 添加內容單元格到卡片
                activityCard.addCell(contentCell);

                doc.add(activityCard);

                // 在每個活動結束後添加一個極細的分隔線
                LineSeparator lineSeparator = new LineSeparator(0.5f, 100, LINE_COLOR, Element.ALIGN_CENTER, -10);
                doc.add(lineSeparator);
            }
        }

        doc.close();
        return baos.toByteArray();
    }

    // ====== 輔助方法 ======

    /** 創建用於基本資訊的 Row */
    private void addInfoRow(PdfPTable table, String label, String value) {
        // Label Cell (Bold)
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        // Value Cell (Normal Text)
        PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_TEXT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}
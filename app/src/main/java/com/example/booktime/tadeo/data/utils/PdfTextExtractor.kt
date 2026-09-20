package com.example.booktime.tadeo.data.utils

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object PdfTextExtractor {

    fun extractText(
        context: Context,
        uri: Uri
    ): String {

        return try {

            PDFBoxResourceLoader.init(context)

            val inputStream =
                context.contentResolver.openInputStream(uri)

            val document = PDDocument.load(inputStream)

            val stripper = PDFTextStripper()

            stripper.startPage = 1
            stripper.endPage = document.numberOfPages

            val text = stripper.getText(document)

            document.close()
            inputStream?.close()

            text

        } catch (e: Exception) {
            "No se pudo extraer texto del PDF"
        }
    }
}
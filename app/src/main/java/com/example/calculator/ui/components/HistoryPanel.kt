package com.example.calculator.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalcTheme
import com.example.calculator.viewmodel.HistoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryPanel(
    history: List<HistoryEntry>,
    theme: CalcTheme,
    onUse: (HistoryEntry) -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .systemBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HISTORY",
                color = theme.textAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (history.isNotEmpty()) {
                    SmallChip("CLEAR", theme, onClick = onClear)
                }
                SmallChip("✕", theme, onClick = onClose)
            }
        }

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("∅", color = theme.textDim, fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No calculations yet", color = theme.textDim, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history, key = { it.timestamp }) { entry ->
                    HistoryCard(entry, theme, onClick = { onUse(entry) })
                }
            }
        }
    }
}

@Composable
fun HistoryCard(entry: HistoryEntry, theme: CalcTheme, onClick: () -> Unit) {
    val timeLabel = remember(entry.timestamp) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(entry.timestamp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(theme.btnCornerRadius))
            .background(theme.historyCardBg)
            .border(theme.borderWidth, theme.borderColor, RoundedCornerShape(theme.btnCornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    timeLabel,
                    color = theme.textDim,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    "tap to reuse →",
                    color = theme.textDim,
                    fontSize = 9.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                entry.expression,
                color = theme.textExpression,
                fontSize = 13.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "= ${entry.result}",
                color = theme.historyAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmallChip(label: String, theme: CalcTheme, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(theme.chipBg)
            .border(theme.borderWidth, theme.chipBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, color = theme.chipText, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

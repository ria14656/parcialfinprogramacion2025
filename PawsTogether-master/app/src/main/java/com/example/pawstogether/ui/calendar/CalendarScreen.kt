package com.example.pawstogether.ui.calendar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.launch

// Saver para evitar crash con LocalDate en rememberSaveable
private val LocalDateSaver: Saver<LocalDate, Long> = Saver(
    save = { it.toEpochDay() },
    restore = { LocalDate.ofEpochDay(it) }
)

/* Tipos y modelo */
enum class EventType { MEDICAL, GROOMING, OTHER }

data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val time: LocalTime? = null,
    val type: EventType = EventType.OTHER
)

/* Pantalla */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { DayOfWeek.MONDAY }

    val calState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //  Saver para LocalDate (evita crash al restaurar estado)
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(LocalDate.now())
    }

    // Eventos en memoria (luego lo conectamos a Firestore)
    var events by remember { mutableStateOf(generateSampleEvents()) }

    // Filtros
    var showMedical by rememberSaveable { mutableStateOf(true) }
    var showGrooming by rememberSaveable { mutableStateOf(true) }
    var showOther by rememberSaveable { mutableStateOf(true) }

    // Diálogo de creación
    var showCreateDialog by remember { mutableStateOf(false) }

    // Orden: nulos al final usando LocalTime.MAX
    val dayEvents = remember(selectedDate, events, showMedical, showGrooming, showOther) {
        events
            .filter { it.date == selectedDate }
            .filter {
                when (it.type) {
                    EventType.MEDICAL -> showMedical
                    EventType.GROOMING -> showGrooming
                    EventType.OTHER -> showOther
                }
            }
            .sortedWith(
                compareBy<CalendarEvent> { it.time ?: LocalTime.MAX }
                    .thenBy { it.title }
            )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val visibleYm = calState.firstVisibleMonth.yearMonth
                    Text(
                        "${visibleYm.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }} ${visibleYm.year}"
                    )
                },
                actions = {
                    TextButton(onClick = {
                        selectedDate = LocalDate.now()
                        scope.launch { calState.scrollToMonth(YearMonth.from(LocalDate.now())) }
                    }) { Text("Hoy") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva cita")
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Controles de mes
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    scope.launch {
                        val target = calState.firstVisibleMonth.yearMonth.minusMonths(1)
                        calState.scrollToMonth(target)
                    }
                }) { Text("◀ Mes anterior") }

                TextButton(onClick = {
                    scope.launch {
                        val target = calState.firstVisibleMonth.yearMonth.plusMonths(1)
                        calState.scrollToMonth(target)
                    }
                }) { Text("Mes siguiente ▶") }
            }

            // Días de la semana
            DaysOfWeekTitle(firstDayOfWeek)

            // Calendario
            HorizontalCalendar(
                state = calState,
                dayContent = { day ->
                    val visibleTypes = events.filter { it.date == day.date }.map { it.type }.toSet()
                    DayCell(
                        day = day,
                        isSelected = day.date == selectedDate,
                        dots = buildList {
                            if (visibleTypes.contains(EventType.MEDICAL) && showMedical) add(EventType.MEDICAL)
                            if (visibleTypes.contains(EventType.GROOMING) && showGrooming) add(EventType.GROOMING)
                            if (visibleTypes.contains(EventType.OTHER) && showOther) add(EventType.OTHER)
                        },
                    ) { clicked ->
                        selectedDate = clicked
                    }
                },
                monthHeader = { /* encabezado ya está en AppBar */ }
            )

            // Filtros
            FilterRow(
                medical = showMedical, onMedical = { showMedical = it },
                grooming = showGrooming, onGrooming = { showGrooming = it },
                other = showOther, onOther = { showOther = it }
            )

            // Lista del día
            Spacer(Modifier.height(8.dp))
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (dayEvents.isEmpty()) {
                Text(
                    "Sin eventos",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(dayEvents, key = { it.id }) { evt ->
                        EventItem(evt)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Diálogo crear evento
    if (showCreateDialog) {
        CreateEventDialog(
            date = selectedDate,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, timeStr, type ->
                val time = timeStr
                    ?.takeIf { it.isNotBlank() }
                    ?.runCatching { LocalTime.parse(this) }
                    ?.getOrNull()

                val new = CalendarEvent(
                    id = "${selectedDate}_${System.currentTimeMillis()}",
                    title = title.ifBlank { defaultTitle(type) },
                    date = selectedDate,
                    time = time,
                    type = type
                )
                events = events + new
                showCreateDialog = false
                Toast.makeText(context, "Cita creada", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/*  auxiliares  */

@Composable
private fun DaysOfWeekTitle(firstDayOfWeek: DayOfWeek) {
    val days = remember(firstDayOfWeek) {
        generateSequence(firstDayOfWeek) { it.plus(1) }.take(7).toList()
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { day ->
            Text(
                day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    dots: List<EventType>,
    onClick: (LocalDate) -> Unit
) {
    val isMonthDate = day.position == DayPosition.MonthDate
    val textColor = when {
        !isMonthDate -> MaterialTheme.colorScheme.onSurfaceVariant
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val base = if (isSelected) {
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    } else {
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    }

    Box(
        modifier = base.clickable(enabled = isMonthDate) { onClick(day.date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        // Hasta 3 puntos (uno por tipo)
        if (dots.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dots.take(3).forEach { type ->
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(typeColor(type))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(
    medical: Boolean, onMedical: (Boolean) -> Unit,
    grooming: Boolean, onGrooming: (Boolean) -> Unit,
    other: Boolean, onOther: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = medical,
            onClick = { onMedical(!medical) },
            label = { Text("Médicas") },
            leadingIcon = { Dot(typeColor(EventType.MEDICAL)) }
        )
        FilterChip(
            selected = grooming,
            onClick = { onGrooming(!grooming) },
            label = { Text("Grooming") },
            leadingIcon = { Dot(typeColor(EventType.GROOMING)) }
        )
        FilterChip(
            selected = other,
            onClick = { onOther(!other) },
            label = { Text("Otros") },
            leadingIcon = { Dot(typeColor(EventType.OTHER)) }
        )
    }
}

@Composable
private fun Dot(color: androidx.compose.ui.graphics.Color) {
    Box(
        Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun EventItem(evt: CalendarEvent) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot(typeColor(evt.type))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(evt.title, style = MaterialTheme.typography.titleMedium)
                val info = buildString {
                    append(evt.type.toFriendly())
                    evt.time?.let { append(" · $it") }
                }
                Text(info, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

/* Diálogo de creación  */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEventDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onCreate: (title: String, timeHHmm: String?, type: EventType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") } // formato HH:mm (opcional)
    var expanded by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(EventType.MEDICAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva cita (${date.dayOfMonth}/${date.monthValue})") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Hora (HH:mm) opcional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown simple compatible con todas las versiones de M3
                Box {
                    OutlinedTextField(
                        value = type.toFriendly(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        EventType.values().forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt.toFriendly()) },
                                onClick = {
                                    type = opt
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(title.trim(), time.trim().ifBlank { null }, type) }) {
                Text("Crear")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

/* --------- Utilidades --------- */

private fun EventType.toFriendly(): String = when (this) {
    EventType.MEDICAL -> "Médica"
    EventType.GROOMING -> "Grooming"
    EventType.OTHER -> "Otro"
}

@Composable
private fun typeColor(type: EventType) = when (type) {
    EventType.MEDICAL -> MaterialTheme.colorScheme.error
    EventType.GROOMING -> MaterialTheme.colorScheme.tertiary
    EventType.OTHER -> MaterialTheme.colorScheme.secondary
}

private fun defaultTitle(type: EventType) = when (type) {
    EventType.MEDICAL -> "Cita médica"
    EventType.GROOMING -> "Grooming"
    EventType.OTHER -> "Evento"
}

/* ------- Datos de muestra (demo) ------- */
private fun generateSampleEvents(): List<CalendarEvent> {
    val today = LocalDate.now()
    val rnd = Random(System.currentTimeMillis())
    val types = listOf(EventType.MEDICAL, EventType.GROOMING, EventType.OTHER)
    return buildList {
        repeat(18) { idx ->
            val day = today.plusDays(rnd.nextLong(-15, 20))
            val type = types[rnd.nextInt(types.size)]
            add(
                CalendarEvent(
                    id = "e$idx",
                    title = defaultTitle(type),
                    date = day,
                    time = if (rnd.nextBoolean()) LocalTime.of(rnd.nextInt(8, 20), 0) else null,
                    type = type
                )
            )
        }
    }
}

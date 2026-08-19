# ExpenseTracker

A native Android app for tracking monthly household expenses — built with Kotlin and Jetpack Compose.

## Features

- **Monthly overview** — net balance, spending, and income for the current month, with navigation to past and future months
- **Top categories** — at-a-glance breakdown of where your money went this month, ranked by amount
- **Daily activity feed** — every transaction for the month, with a flag on unusually large expenses (configurable threshold)
- **Categories** — Bills & Utilities, Food & Drinks, Grocery, Health, Personal Care, Petrol, Saving, Shopee Payment, Loans, Others
- **Payer split** — tag each expense as *Me*, *Wife*, or *Split*, and filter history by who paid
- **History** — full all-time transaction ledger, grouped by month, filterable by payer and category
- **Trends** — 6-month expense vs. income bar chart, average monthly spend, best saving month, category pie chart, payer breakdown
- **Dark / light / system theme**
- **Currency picker** — MYR, USD, SGD, EUR, GBP, IDR, INR, JPY presets or a custom symbol, with an option to hide the symbol entirely
- **Export / Import** — back up all transactions to JSON or CSV, or restore from a JSON backup
- **Google Sheets sync** — planned, not yet implemented (see [Roadmap](#roadmap))

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** for local persistence
- **DataStore** for settings (theme, currency, threshold)
- **Navigation Compose** for the bottom-tab navigation
- Custom Canvas-based bar/pie charts (no external charting dependency)
- **kotlinx.serialization** for JSON export/import

Minimum SDK 26, target/compile SDK 35, Kotlin 2.0.21.

## Project structure

```
app/src/main/java/com/expensetracker/app/
├── data/            # Room entities/DAO, DataStore settings, export-import, categories
├── repository/      # Repository layer over the DAO
├── ui/
│   ├── components/  # Reusable composables (charts, cards, list items, add/edit sheet)
│   ├── dashboard/    # Home tab
│   ├── transactions/ # History tab
│   ├── trends/       # Trends tab
│   ├── settings/     # Settings tab
│   ├── navigation/   # Bottom nav + NavHost
│   └── theme/         # Color scheme, typography
├── AppViewModel.kt    # Single shared ViewModel holding app state
└── MainActivity.kt
```

## Building

Requires the Android SDK and a JDK 17+ (the Android Studio bundled JBR works).

```bash
JAVA_HOME="/path/to/jdk" ./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. Install with:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Roadmap

- [ ] Google Sheets sync (OAuth sign-in + push/pull transactions) — stubbed in `data/SheetsSyncManager.kt`
- [ ] Recurring/monthly bills
- [ ] Per-category budget goals
- [ ] Search within History

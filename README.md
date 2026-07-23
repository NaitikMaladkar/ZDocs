# ZDocs

A modern, lightweight Android file viewer and editor built with Kotlin + Jetpack Compose.

## Features

- 📄 **Documents** — View and edit DOCX, ODT, TXT, MD files
- 📊 **Spreadsheets** — Grid-based editor for XLSX, XLS, CSV, ODS files
- 🎞️ **Presentations** — View PPTX, ODP files
- 📕 **PDF** — View PDF files
- 📝 **Notes** — Quick notes and memos (TXT, MD)
- 💻 **Code** — Syntax highlighting for 15+ languages
- 🗃️ **Data** — JSON, XML, YAML, config files
- 🎨 **Material 3 UI** — Clean minimal design with Teal/Green accents
- 🌙 **Dark Mode** — Persistent toggle saved to preferences
- ⭐ **Favorites** — Star your important files for quick access
- 🔄 **Rename Files** — Rename directly from context menu
- 📤 **Share Files** — Share via Android share sheet
- 🔍 **File Handler** — Open files from any app with "Open with ZDocs"
- 📂 **Smart Storage** — Files auto-organized into type-based folders
- ➕ **File Templates** — Create from categories: Text Document, Spreadsheet, Presentation, PDF, Form, Whiteboard
- 🗂️ **Bottom Navigation** — Home, Recent, Favorites, Settings

## Storage Structure

```
Android/Data/com.zdocs/files/
└── ZDocs/
    ├── Documents/       (.docx, .odt, .txt, .md)
    ├── Spreadsheets/    (.xlsx, .xls, .csv, .ods)
    ├── Presentations/   (.pptx, .odp)
    ├── PDFs/            (.pdf)
    ├── Notes/           (.txt, .md)
    ├── Code/            (.py, .js, .ts, .html, .css, .kt, .java, etc.)
    └── Data/            (.json, .xml, .yaml, .yml, .toml, .ini, etc.)
```

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Storage:** Local file system (no database)

## Roadmap

- [ ] DOCX rendering and editing
- [ ] ODT rendering and editing
- [ ] XLSX full rendering
- [ ] PPTX rendering and editing
- [ ] PDF annotation
- [ ] Markdown preview mode with live rendering
- [ ] Collaborative editing
- [ ] Export to PDF
- [ ] File search and filtering across all types
- [ ] Biometric lock for sensitive files
- [ ] Undo/Redo in text editor
- [ ] Auto-save with configurable interval
- [ ] File sorting (name, date, size, type)
- [ ] Batch operations (select multiple files)
- [ ] Find and replace in editor
- [ ] Custom themes / accent colors

## License

MIT License

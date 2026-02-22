# Betting Manager Android App

A professional betting management application built with modern Android technologies.

## Features

### Core Functionality
- **Entry Tab**: Create and manage betting entries with real-time calculations
- **Vouchers Tab**: Track betting vouchers with status (Pending/Forwarded/Cleared)
- **Master History Tab**: Historical view of forwarded vouchers with reversal capability
- **Report Tab**: Generate self and master reports with statistics
- **Real-time Dashboard**: Live updates of sales, commission, payouts, and profit

### Technical Features
- **Modern Architecture**: MVVM + Repository pattern with Hilt dependency injection
- **Database**: SQLite with Room ORM for local data persistence
- **UI**: Jetpack Compose with Material 3 design system
- **Real-time Updates**: Coroutines and Flow for reactive programming
- **Security**: Input validation, SQL injection prevention, secure data handling
- **Performance**: Optimized queries, efficient state management

## Architecture

```
app/
├── database/           # Room entities and DAOs
├── repository/         # Business logic and data layer
├── parsing/           # Bet parsing engine
├── ui/                # Jetpack Compose UI components
├── di/                # Hilt dependency injection
└── theme/             # Material 3 theming
```

## Setup

### Prerequisites
- Android Studio Flamingo (2022.2.1) or later
- JDK 17 or later
- Android SDK 34 (Android 14)

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/yourusername/betting-manager-app.git

# Open in Android Studio
# Or build from command line
./gradlew assembleDebug
```

### GitHub Actions CI/CD
The project includes automated CI/CD with:
- Automated builds on push/PR
- Test execution and coverage reports
- Lint checks and code quality analysis
- Release APK generation with QR codes
- Security vulnerability scanning

## Usage

### Creating a Bet Entry
1. Navigate to the Entry tab
2. Enter bet numbers (e.g., "123 456" for direct, "123*" for rolled)
3. View real-time calculations
4. Create voucher to save the entry

### Managing Vouchers
- View all vouchers in the Vouchers tab
- Edit, delete, or forward vouchers
- Track status with color-coded badges

### Generating Reports
- Access the Report tab for statistics
- Filter by date ranges
- Export reports for analysis

## Development

### Adding New Features
1. Create database entities in `database/`
2. Add DAO methods for data access
3. Implement repository logic in `repository/`
4. Create UI components in `ui/`
5. Add ViewModel for state management

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew testDebugUnitTestCoverage
```

## Security

- SQL injection prevention through parameterized queries
- Input validation and sanitization
- Secure token handling for API calls
- Data encryption at rest
- Permission management

## Performance

- Database queries optimized (< 100ms response time)
- UI rendering at 60 FPS target
- Memory usage < 100MB typical
- Cold start < 2 seconds

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests and lint checks
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and feature requests, please use the GitHub Issues page.

## Acknowledgments

- Built with Jetpack Compose and Material 3
- Uses Room for database persistence
- Hilt for dependency injection
- GitHub Actions for CI/CD automation
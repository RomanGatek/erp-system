# Java ERP System

## Overview
This Enterprise Resource Planning (ERP) system is a comprehensive business management solution designed to streamline operations across various departments within an organization. Built with Java, this application provides a robust, scalable, and secure platform for managing business processes including inventory, sales, purchasing, accounting, human resources, and customer relationship management.

## Current Implementation Status
The following features are currently implemented and ready to use:
- User authentication and authorization system
- Basic inventory management module
- Product catalog with CRUD operations
- Simple reporting dashboard

## Features
- **User Management**: Role-based access control with customizable permissions
- ~~**Inventory Management**: Track stock levels, orders, and deliveries~~
- ~~**Sales Management**: Manage the entire sales cycle from quotation to invoicing~~
- **Purchasing**: Streamline procurement processes
- ~~**Financial Management**: General ledger, accounts receivable/payable~~
- ~~**Human Resources**: Employee records, attendance, and payroll~~
- **Reporting**: Comprehensive reporting tools with customizable dashboards
- ~~**Integration Capabilities**: APIs for third-party system integration~~

## Technologies
- Java JDK 21-23
- Spring Framework (Spring Boot, Spring Data, Spring Security)
- Hibernate/JPA
- RESTful API architecture
- MySQL database
- Gradle for dependency management
- **Frontend**: Modern JavaScript with Vue, ESLint, and Vitest for testing

## Prerequisites
- JDK 21 or higher
- Gradle 7.0+
- MySQL 8.0+
- Node.js 16+ (for frontend development)
- Bun (for package management)

## Installation

### Backend Setup
1. Clone the repository:
   ```
   git clone https://github.com/username/erp-system.git
   cd erp-system
   ```

2. Configure the database in `src/main/resources/application.properties`

3. Build the project:
   ```
   ./gradlew clean build
   ```

4. Run the application:
   ```
   ./gradlew bootRun
   ```
   
5. Access the backend API at `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend directory:
   ```
   cd frontend
   ```

2. Install dependencies:
   ```
   bun install
   ```

3. Run the development server:
   ```
   bun run dev
   ```

4. Access the frontend application at `http://localhost:5173`

## Project Structure
```
erp-system/
├── src/                # Backend source files
│   ├── main/
│   │   ├── java/       # Java source files
│   │   ├── resources/  # Configuration files
│   ├── test/           # Test files
├── frontend/           # Frontend application
│   ├── src/            # Frontend source files
│   ├── public/         # Static assets
│   ├── tests/          # Frontend tests
│   └── ...             # Frontend configuration files
├── docs/               # Documentation
└── build.gradle        # Gradle configuration
```

## Configuration
The system can be configured through the following files:
- `application.properties`: Main application configuration
- `application-dev.properties`: Development environment settings
- `application-prod.properties`: Production environment settings
- `frontend/.env`: Frontend environment variables

## API Documentation
API documentation is available at `http://localhost:8080/swagger-ui.html` when running the application.

## Development
### Backend Development
#### Adding New Modules
1. Create a new package under the appropriate domain
2. Implement the required interfaces
3. Register the module in the application context

#### Database Migrations
Database migrations are managed through Flyway. Create new migration scripts in `src/main/resources/db/migration`.

### Frontend Development
1. Component structure follows a modular approach
2. State management is handled through context API
3. API communication is centralized in service files

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Support
For support, please contact the development team or open an issue in the GitHub repository. 

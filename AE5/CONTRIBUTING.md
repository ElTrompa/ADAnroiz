# Contributing to AE5 Warranty System

Thank you for your interest in contributing to the AE5 Warranty Management System!

## Code of Conduct

- Be respectful and inclusive
- Focus on the code, not the person
- Provide constructive feedback
- Help others learn and grow

## Getting Started

### 1. Set Up Development Environment

```bash
# Clone the repository
git clone https://github.com/yourusername/AE5.git
cd AE5

# Create a feature branch
git checkout -b feature/your-feature-name

# Install dependencies
mvn clean install

# Start Docker services
docker-compose up -d

# Run the application
mvn javafx:run
```

### 2. Code Style Guide

#### Java Code Style
```java
// Use consistent naming
public class GuaranteeService {
    // Private fields with underscore prefix (optional)
    private final GuaranteeRepository repository;
    
    // Methods in camelCase
    public List<Guarantee> getGuaranteesByStatus(String status) {
        // Indent with 4 spaces
        List<Guarantee> results = new ArrayList<>();
        return results;
    }
}

// Use Lombok annotations to reduce boilerplate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "guarantees")
public class Guarantee {
    // Class implementation
}
```

#### Naming Conventions
- **Classes:** PascalCase (`GuaranteeService`, `MainViewController`)
- **Methods:** camelCase (`getGuaranteeById`, `createGuarantee`)
- **Variables:** camelCase (`clientName`, `warrantyEndDate`)
- **Constants:** UPPER_SNAKE_CASE (`DEFAULT_TIMEOUT`, `MAX_RETRIES`)

### 3. Commit Guidelines

Write clear, descriptive commit messages:

```bash
# Good
git commit -m "feat: Add warranty status update functionality"
git commit -m "fix: Resolve MongoDB connection timeout issue"
git commit -m "docs: Update API documentation"

# Avoid
git commit -m "Fixed stuff"
git commit -m "Updated files"
```

**Commit Types:**
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Tests
- `chore:` Build, dependencies, etc.

### 4. Testing

All contributions should include tests:

```bash
# Run existing tests
mvn test

# Run specific test class
mvn test -Dtest=GuaranteeServiceTest

# Run with coverage
mvn test jacoco:report
```

**Test Coverage Goals:**
- Service layer: >80%
- Repository layer: >85%
- Controller layer: >70%
- UI layer: >50% (if applicable)

### 5. Pull Request Process

1. **Before submitting:**
   - Ensure tests pass: `mvn test`
   - Format code: `mvn spotless:apply`
   - Update documentation if needed

2. **Create a pull request:**
   - Use a descriptive title
   - Reference related issues
   - Describe what changed and why
   - Include screenshots for UI changes

3. **Example PR description:**
   ```
   ## Description
   Add ability to update warranty status from ACTIVE to EXPIRED
   
   ## Related Issue
   Closes #42
   
   ## Changes Made
   - Added `updateGuaranteeStatus()` method to GuaranteeService
   - Updated status ComboBox in GuaranteeTab
   - Added validation for date transitions
   
   ## Testing
   - Added unit tests in GuaranteeServiceTest
   - Tested manual status update via UI
   
   ## Screenshots
   [Show warranty status change workflow]
   ```

## Area Guidelines

### Adding a New Feature

**Example: Adding warranty email notifications**

1. **Create the model/entity:**
   ```java
   @Data
   @Document(collection = "notifications")
   public class WarrantyNotification {
       // Fields
   }
   ```

2. **Create repository:**
   ```java
   @Repository
   public interface NotificationRepository extends MongoRepository<WarrantyNotification, String> {
       List<WarrantyNotification> findByGuaranteeId(String guaranteeId);
   }
   ```

3. **Create service:**
   ```java
   @Service
   public class NotificationService {
       public void sendExpirationReminder(Guarantee guarantee) {
           // Implementation
       }
   }
   ```

4. **Update UI if needed:**
   ```java
   // Add button in MainViewController
   Button notifyButton = new Button("Send Notification");
   notifyButton.setOnAction(e -> {
       Guarantee selected = table.getSelectionModel().getSelectedItem();
       notificationService.sendExpirationReminder(selected);
   });
   ```

5. **Write tests:**
   ```java
   @Test
   void testSendExpirationReminder() {
       // Test implementation
   }
   ```

### Fixing a Bug

1. **Reproduce the issue:**
   - Document the steps to reproduce
   - Add a test that fails
   - Fix the code
   - Verify the test passes

2. **Example:**
   ```java
   // Test that catches the bug
   @Test
   void testWarrantyDateValidation() {
       // This test should fail before the fix
       Guarantee g = new Guarantee();
       g.setWarrantyStart(LocalDate.of(2025, 12, 31));
       g.setWarrantyEnd(LocalDate.of(2025, 01, 01));
       
       assertThrows(InvalidGuaranteeException.class, () -> {
           guaranteeService.createGuarantee(g);
       });
   }
   ```

### Documentation

- Keep README.md updated
- Add JavaDoc comments to public methods:
  ```java
  /**
   * Retrieves all warranties for a specific client
   * 
   * @param client the client name to search for
   * @return list of warranties matching the client
   * @throws IllegalArgumentException if client is null or empty
   */
  public List<Guarantee> getGuaranteesByClient(String client) {
      // Implementation
  }
  ```

## Common Development Tasks

### Adding a new database field to Guarantee

1. **Update the model:**
   ```java
   @Data
   @Document(collection = "guarantees")
   public class Guarantee {
       // ... existing fields ...
       private String serialNumber;  // NEW FIELD
   }
   ```

2. **Add migration (if needed):**
   MongoDB auto-creates fields, but add to init script if it's important:
   ```javascript
   db.guarantees.updateMany({}, { $set: { serialNumber: "" } });
   ```

3. **Update UI forms:**
   ```java
   Label serialLabel = new Label("Serial Number:");
   TextField serialField = new TextField();
   serialField.setPromptText("Product serial number");
   root.getChildren().addAll(serialLabel, serialField);
   ```

4. **Add tests:**
   ```java
   @Test
   void testGuaranteeWithSerialNumber() {
       Guarantee g = new Guarantee();
       g.setSerialNumber("ABC123XYZ");
       Guarantee saved = guaranteeService.createGuarantee(g);
       assertEquals("ABC123XYZ", saved.getSerialNumber());
   }
   ```

### Integrating a new API (e.g., email service)

1. **Add dependency to pom.xml:**
   ```xml
   <dependency>
       <groupId>com.sendgrid</groupId>
       <artifactId>sendgrid-java</artifactId>
       <version>4.9.3</version>
   </dependency>
   ```

2. **Create configuration:**
   ```yaml
   # application.yml
   sendgrid:
     api-key: ${SENDGRID_API_KEY}
     from-email: notifications@ae5-warranty.com
   ```

3. **Create service:**
   ```java
   @Service
   public class EmailService {
       @Value("${sendgrid.api-key}")
       private String apiKey;
       
       public void sendWarrantyExpiration(String email, Guarantee g) {
           // Implementation using SendGrid API
       }
   }
   ```

4. **Write integration test:**
   ```java
   @SpringBootTest
   class EmailServiceTest {
       @Autowired
       private EmailService emailService;
       
       @Test
       void testSendEmail() {
           // Mock SendGrid or use test API key
           emailService.sendWarrantyExpiration("test@example.com", guarantee);
       }
   }
   ```

## Troubleshooting Development Issues

### MongoDB connection fails
```bash
# Check if MongoDB is running
docker ps | grep mongodb

# Restart MongoDB
docker-compose restart mongodb

# Check connection in code
docker exec ae5-mongodb mongosh ae5_warranty_db --eval "db.guarantees.count()"
```

### Odoo API unreachable
```bash
# Check Odoo logs
docker logs ae5-odoo | grep -i error

# Verify port
netstat -an | grep 8069

# Test connection manually
python3 << 'EOF'
import xmlrpc.client
try:
    common = xmlrpc.client.ServerProxy("http://localhost:8069/xmlrpc/2/common")
    uid = common.authenticate("odoo", "admin", "admin", {})
    print(f"Success: uid={uid}")
except Exception as e:
    print(f"Failed: {e}")
EOF
```

### JavaFX not displaying
```bash
# On WSL2/Linux without display
export DISPLAY=:0
mvn javafx:run

# Check Java version
java -version  # Should be 21+

# Rebuild
mvn clean compile
mvn javafx:run
```

## Questions or Need Help?

- Check existing issues: GitHub Issues
- Review documentation: README.md, ARCHITECTURE.md
- Ask in pull request comments
- Create a discussion thread

## License

By contributing, you agree that your contributions will be licensed under the project's existing license.

---

**Thank you for contributing to AE5!** 🎉

**Last Updated:** January 2026

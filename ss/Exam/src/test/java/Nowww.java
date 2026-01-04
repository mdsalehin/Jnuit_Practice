import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Nowww
{
    WebDriver driverobj;
    WebDriverWait wait;

    @BeforeAll
    public void setup()
    {
        driverobj = new ChromeDriver();
        driverobj.manage().window().maximize();
        driverobj.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        wait = new WebDriverWait(driverobj, Duration.ofSeconds(15));

        //  LOGIN ONCE
        driverobj.get("https://opensource-demo.orangehrmlive.com/");
        driverobj.findElements(By.className("oxd-input")).get(0).sendKeys("Admin");
        driverobj.findElements(By.className("oxd-input")).get(1).sendKeys("admin123");
        driverobj.findElements(By.cssSelector("[type=submit]")).get(0).click();

        // confirm login
        Assertions.assertTrue(
                driverobj.findElement(By.className("oxd-userdropdown-name")).isDisplayed()
        );
    }

    @Test
    public void dashboardValidationValidation()
    {
        List<WebElement> userInfo =
                driverobj.findElements(By.className("oxd-topbar-header-breadcrumb"));

        String userLoginActual   = userInfo.get(0).getText();
        String userLoginExpected = "Dashboard";

        System.out.println("Actual   : " + userLoginActual);
        System.out.println("Expected : " + userLoginExpected);

        Assertions.assertTrue(userLoginActual.contains(userLoginExpected));
    }

    @Test
    public void adminValidation()
    {
        List<WebElement> adminInfo =
                driverobj.findElements(By.className("oxd-main-menu-item--name"));

        String userAdminActual   = adminInfo.get(0).getText();
        String userAdminExpected = "Admin";

        System.out.println("Actual   : " + userAdminActual);
        System.out.println("Expected : " + userAdminExpected);

        Assertions.assertTrue(userAdminActual.contains(userAdminExpected));
    }

    @Test
    public void pimValidation()
    {
        List<WebElement> pimManagementInfo =
                driverobj.findElements(By.className("oxd-main-menu-item--name"));

        String pimManagementActual   = pimManagementInfo.get(1).getText();
        String pimManagementExpected = "PIM";

        System.out.println("Actual   : " + pimManagementActual);
        System.out.println("Expected : " + pimManagementExpected);

        Assertions.assertTrue(pimManagementActual.contains(pimManagementExpected));
    }

    @Test
    public void userProfileNameValidation()
    {
        List<WebElement> userProfileInfo =
                driverobj.findElements(By.className("oxd-userdropdown-name"));

        String userProfileActual = userProfileInfo.get(0).getText();

        System.out.println("User Profile Name : " + userProfileActual);

        Assertions.assertFalse(userProfileActual.isEmpty());
    }

    @Test
    public void pimEmployeeTableValidation()
    {
        //  Navigate to PIM module
        driverobj.findElements(By.className("oxd-main-menu-item--name"))
                .stream()
                .filter(e -> e.getText().trim().equals("PIM"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("PIM menu not found"))
                .click();

        //  Select Employment Status = Full-Time Permanent
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='Employment Status']/../following-sibling::div//i")
        )).click();

        List<WebElement> options = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//div[@role='listbox']//span")
                )
        );

        boolean fullTimePermanentSelected = false;

        for (WebElement option : options)
        {
            if (option.getText().trim().equals("Full-Time Permanent"))
            {
                option.click();
                fullTimePermanentSelected = true;
                break;
            }
        }

        Assertions.assertTrue(
                fullTimePermanentSelected,
                "Full-Time Permanent status not available in filter"
        );

        // Click Search
        driverobj.findElement(By.xpath("//button[.=' Search ']")).click();

        //  Wait for employee table
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-table-body .oxd-table-row")
        ));

        List<WebElement> rows =
                driverobj.findElements(By.cssSelector(".oxd-table-body .oxd-table-row"));

        // At least one employee must exist with this status
        Assertions.assertFalse(
                rows.isEmpty(),
                "No employee found with Full-Time Permanent status"
        );

        // Extract first 5 employee names
        int limit = Math.min(5, rows.size());

        for (int i = 0; i < limit; i++)
        {
            List<WebElement> cells =
                    rows.get(i).findElements(By.className("oxd-table-cell"));

            String firstName  = cells.get(1).getText().trim();
            String middleName = cells.get(2).getText().trim();
            String lastName   = cells.get(3).getText().trim();

            String fullName =
                    (firstName + " " + middleName + " " + lastName).trim();

            System.out.println("Employee " + (i + 1) + " : " + fullName);

            //  No name should be empty
            Assertions.assertFalse(
                    fullName.isEmpty(),
                    "Employee name is empty at row " + (i + 1)
            );
        }
    }


   @AfterAll
    public void quitDriver()
    {
        driverobj.quit();
    }
}

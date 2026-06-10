# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: tests\ui.spec.js >> GramFlow Full UI E2E Tests - 100% Coverage >> 1. Landing Page - Renders hero, features, and navigates to Login
- Location: tests\ui.spec.js:7:3

# Error details

```
Error: expect(locator).toBeVisible() failed

Locator: locator('text=Stop missing out on sales').first()
Expected: visible
Timeout: 5000ms
Error: element(s) not found

Call log:
  - Expect "toBeVisible" with timeout 5000ms
  - waiting for locator('text=Stop missing out on sales').first()

```

```yaml
- navigation:
  - text: GramFlow
  - link "Log In":
    - /url: /login
    - button "Log In"
  - link "Get Started":
    - /url: /login
    - button "Get Started"
- text: Official Meta Business Partner
- heading "Automate Your Instagram Engagement Like Never Before" [level=1]
- paragraph: GramFlow helps agencies and creators scale their reach with intelligent keyword triggers, automated DM replies, and deep audience insights.
- link "Start Free Trial":
  - /url: /login
  - button "Start Free Trial"
- button "Book a Demo"
- img "GramFlow Dashboard"
- heading "Smart Auto-Replies" [level=3]
- paragraph: Set up keyword-based triggers to automatically respond to DMs and comments in seconds.
- heading "Lead Generation" [level=3]
- paragraph: Capture high-intent leads from your Instagram engagement and sync them directly to your CRM.
- heading "Deep Analytics" [level=3]
- paragraph: Track engagement rates, trigger performance, and audience growth with beautiful, real-time charts.
- heading "Trusted by 10,000+ Brands" [level=2]
- text: Meta Official Graph API
- paragraph: "\"GramFlow transformed how we manage our client accounts. We've seen a 400% increase in lead conversion since implementing their automated flows.\""
- paragraph: Sarah Jenkins
- paragraph: Director of Social, Velocity Digital
- contentinfo:
  - text: GramFlow
  - link "Privacy":
    - /url: /
  - link "Terms":
    - /url: /
  - link "API Docs":
    - /url: /
  - link "Contact":
    - /url: /
  - paragraph: © 2026 GramFlow Inc. All rights reserved.
```

# Test source

```ts
  1   | const { test, expect } = require('@playwright/test');
  2   | 
  3   | test.describe('GramFlow Full UI E2E Tests - 100% Coverage', () => {
  4   | 
  5   |   // --- UNAUTHENTICATED ROUTES ---
  6   |   
  7   |   test('1. Landing Page - Renders hero, features, and navigates to Login', async ({ page }) => {
  8   |     await page.goto('http://localhost:5173');
  9   |     await expect(page).toHaveTitle(/frontend/i);
  10  |     
  11  |     // Check Hero Section
  12  |     await expect(page.locator('h1', { hasText: 'Automate Your' })).toBeVisible();
> 13  |     await expect(page.locator('text=Stop missing out on sales').first()).toBeVisible();
      |                                                                          ^ Error: expect(locator).toBeVisible() failed
  14  |     
  15  |     // Click Get Started (First one)
  16  |     const getStartedBtn = page.locator('a[href="/login"]').first();
  17  |     await expect(getStartedBtn).toBeVisible();
  18  |     await getStartedBtn.click();
  19  |     await expect(page).toHaveURL(/.*\/login/);
  20  |   });
  21  | 
  22  |   test('2. Login Page - Renders Firebase Google and Email options with validations', async ({ page }) => {
  23  |     await page.goto('http://localhost:5173/login');
  24  |     
  25  |     // Title
  26  |     await expect(page.locator('h3', { hasText: 'Welcome to GramFlow' })).toBeVisible();
  27  |     
  28  |     // Google Login button
  29  |     const googleBtn = page.locator('button:has-text("Continue with Google")');
  30  |     await expect(googleBtn).toBeVisible();
  31  |     
  32  |     // Email form validations
  33  |     const emailInput = page.locator('input[type="email"]');
  34  |     await expect(emailInput).toBeVisible();
  35  |     const submitBtn = page.locator('button:has-text("Send Magic Link")');
  36  |     await expect(submitBtn).toBeDisabled(); 
  37  |     
  38  |     // Type invalid email (HTML5 validation will catch it on submit, but the button should enable as long as it has text based on our logic)
  39  |     await emailInput.fill('invalid-email');
  40  |     await expect(submitBtn).toBeEnabled();
  41  |     
  42  |     // Type valid email
  43  |     await emailInput.fill('test@example.com');
  44  |     await expect(submitBtn).toBeEnabled();
  45  |   });
  46  | 
  47  |   // --- AUTHENTICATED DASHBOARD ROUTES (TEST MOCK) ---
  48  | 
  49  |   test.describe('3. Authenticated Dashboard & Sidebar', () => {
  50  |     test.beforeEach(async ({ page }) => {
  51  |       // Mock Authentication by setting the JWT token directly in localStorage
  52  |       await page.goto('http://localhost:5173');
  53  |       await page.evaluate(() => {
  54  |         window.localStorage.setItem('jwt_token', 'mock_test_token_123');
  55  |       });
  56  |       await page.goto('http://localhost:5173/dashboard');
  57  |     });
  58  | 
  59  |     test('Header & Connect Instagram Button', async ({ page }) => {
  60  |       await expect(page).toHaveURL(/.*\/dashboard\/rules/);
  61  |       
  62  |       // Header Text
  63  |       await expect(page.locator('h1', { hasText: 'Dashboard' })).toBeVisible();
  64  |       
  65  |       // Instagram Button
  66  |       const connectInsta = page.locator('button:has-text("Connect Instagram")');
  67  |       await expect(connectInsta).toBeVisible();
  68  |       
  69  |       // System Online Badge
  70  |       await expect(page.locator('text=System Online')).toBeVisible();
  71  |     });
  72  | 
  73  |     test('Sidebar Navigation completely functional', async ({ page }) => {
  74  |       // Navigate to Insights
  75  |       await page.click('a[href="/dashboard/insights"]');
  76  |       await expect(page).toHaveURL(/.*\/dashboard\/insights/);
  77  |       await expect(page.locator('text=Coming Soon').first()).toBeVisible();
  78  | 
  79  |       // Navigate to Billing
  80  |       await page.click('a[href="/dashboard/billing"]');
  81  |       await expect(page).toHaveURL(/.*\/dashboard\/billing/);
  82  |       await expect(page.locator('text=Coming Soon').first()).toBeVisible();
  83  | 
  84  |       // Navigate back to Rules
  85  |       await page.click('a[href="/dashboard/rules"]');
  86  |       await expect(page).toHaveURL(/.*\/dashboard\/rules/);
  87  |     });
  88  |   });
  89  | 
  90  |   test.describe('4. Automation Rules UI - 100% Coverage', () => {
  91  |     test.beforeEach(async ({ page }) => {
  92  |       await page.goto('http://localhost:5173');
  93  |       await page.evaluate(() => {
  94  |         window.localStorage.setItem('jwt_token', 'mock_test_token_123');
  95  |       });
  96  |       await page.goto('http://localhost:5173/dashboard/rules');
  97  |     });
  98  | 
  99  |     test('Rules rendering, filtering, toggling, and deleting', async ({ page }) => {
  100 |       // 1. Initial Render
  101 |       await expect(page.locator('h2', { hasText: 'Automation Rules' })).toBeVisible();
  102 |       
  103 |       // Check for the MOCK rule "Price Inquiry"
  104 |       await expect(page.locator('h3', { hasText: 'Price Inquiry' })).toBeVisible();
  105 |       await expect(page.locator('span', { hasText: 'PRICE' })).toBeVisible();
  106 | 
  107 |       // 2. Search & Filtering
  108 |       const searchInput = page.locator('input[placeholder="Search rules..."]');
  109 |       await searchInput.fill('Nonexistent Rule');
  110 |       await expect(page.locator('h3', { hasText: 'No Rules Found' })).toBeVisible();
  111 |       
  112 |       await searchInput.fill('Price');
  113 |       await expect(page.locator('h3', { hasText: 'Price Inquiry' })).toBeVisible();
```
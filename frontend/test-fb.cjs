const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', error => console.error('PAGE ERROR:', error));

  console.log("Navigating to http://localhost:5173/dashboard...");
  await page.goto('http://localhost:5173/dashboard');
  
  await page.waitForTimeout(3000);
  
  const fbDefined = await page.evaluate(() => typeof window.FB !== 'undefined');
  console.log("Is window.FB defined?", fbDefined);

  if (fbDefined) {
      const fbInit = await page.evaluate(() => {
          try {
              return Object.keys(window.FB).length > 0;
          } catch(e) { return e.toString(); }
      });
      console.log("FB object keys exist:", fbInit);
  }

  console.log("Simulating click on Connect Instagram button...");
  await page.evaluate(() => {
      const btn = Array.from(document.querySelectorAll('button')).find(b => b.textContent.includes('Connect Instagram'));
      if (btn) btn.click();
      else console.log("Button not found!");
  });

  await page.waitForTimeout(6000);
  
  await browser.close();
})();

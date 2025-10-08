// Diagnostic script: POST to frontend proxy (/api/login) and directly to backend (/login)
(async () => {
  try {
    const fetch = global.fetch || (await import('node-fetch')).default;
    const urlFrontend = 'http://localhost:3000/api/login';
    const urlBackend = 'http://localhost:8080/login';
    const body = { email: 'admin@system.com', password: 'password' };

    console.log('POST to frontend proxy:', urlFrontend);
    let res = await fetch(urlFrontend, {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
    });
    console.log('Frontend proxy status:', res.status);
    console.log('Frontend proxy auth header:', res.headers.get('authorization'));
    const textF = await res.text();
    console.log('Frontend proxy body:', textF.substring(0, 400));

    console.log('\nPOST directly to backend:', urlBackend);
    let res2 = await fetch(urlBackend, {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
    });
    console.log('Backend status:', res2.status);
    console.log('Backend auth header:', res2.headers.get('authorization'));
    const textB = await res2.text();
    console.log('Backend body:', textB.substring(0,400));
  } catch (e) {
    console.error('Error:', e);
    process.exit(1);
  }
})();

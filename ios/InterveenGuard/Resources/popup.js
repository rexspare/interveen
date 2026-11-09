async function requestAccess() {
  try {
    await browser.permissions.request({ origins: ["http://*/*", "https://*/*"] });
  } catch (_) {}
}

requestAccess();
document.getElementById("allow")?.addEventListener("click", requestAccess);

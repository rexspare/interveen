(() => {
  if (globalThis.__interveenGuardBooted) return;
  const api = globalThis.browser || globalThis.chrome;
  const SAFE_URL = "https://www.google.com/";
  const url = location.href;
  if (!/^https?:/i.test(url)) return;
  if (/blocked\.html/i.test(url)) return;

  function pageHaystack() {
    const parts = [location.href, document.title || ""];
    try {
      parts.push(((document.body && document.body.innerText) || "").slice(0, 8000));
    } catch (_) {}
    return parts.join("\n");
  }

  function currentMatch() {
    if (typeof interveenEvaluate !== "function") {
      return { matched: /(?:^|[/.])xxx(?:[/?#:]|$)/i.test(url) || /xxx/i.test(url), evidence: url };
    }
    const fromUrl = interveenEvaluate(location.href);
    if (fromUrl.matched) return fromUrl;
    const host = location.hostname || "";
    if (typeof interveenIsMixedHost === "function" && interveenIsMixedHost(host)) {
      return interveenEvaluate(pageHaystack());
    }
    return { matched: false, evidence: "" };
  }

  let local = currentMatch();
  if (!local.matched) {
    const host = location.hostname || "";
    if (typeof interveenIsMixedHost === "function" && interveenIsMixedHost(host)) {
      let attempts = 0;
      const watch = setInterval(() => {
        local = currentMatch();
        if (local.matched) {
          clearInterval(watch);
          boot();
        } else if (++attempts >= 40) {
          clearInterval(watch);
        }
      }, 500);
    }
    return;
  }

  boot();

  function boot() {
    if (globalThis.__interveenGuardBooted) return;
    local = local.matched ? local : currentMatch();
    if (!local.matched) return;
    globalThis.__interveenGuardBooted = true;
    startGuard();
  }

  function blockedUrl() {
    try {
      return api.runtime.getURL("blocked.html");
    } catch (_) {
      return "";
    }
  }

  function leaveSite() {
    const dest = blockedUrl();
    if (!dest || location.href.startsWith(dest)) return;
    try { location.replace(dest); } catch (_) {}
  }

  let leaving = false;
  function rememberGoal(evidence) {
    if (leaving) return;
    leaving = true;
    try {
      api.runtime.sendMessage({ type: "remembered", evidence: evidence || local.evidence });
    } catch (_) {}
    try { location.assign(SAFE_URL); } catch (_) {
      location.href = SAFE_URL;
    }
  }

  function mountOverlay(goal, force) {
    const host = document.body || document.documentElement;
    if (!host) return;
    let root = document.getElementById("interveen-overlay");
    if (root && !force) return;
    if (!root) {
      root = document.createElement("div");
      root.id = "interveen-overlay";
      root.style.cssText = [
        "position:fixed", "inset:0", "z-index:2147483647", "display:flex",
        "flex-direction:column", "justify-content:space-between",
        "background:#0F172A", "color:#F8FAFC",
        "padding:28px 24px max(32px, env(safe-area-inset-bottom))",
        "font-family:-apple-system,Inter,sans-serif", "box-sizing:border-box",
        "pointer-events:auto",
      ].join(";");
      host.appendChild(root);
    }
    const title = (goal && goal.title) || "Remember why you started.";
    const body = (goal && goal.body) || "This pause is the win.";
    const moment = (goal && goal.moment) || "A moment.";
    const remember = (goal && goal.remember) || "I Remember My Goal";
    const evidence = (goal && goal.evidence) || local.evidence;
    root.replaceChildren();
    const momentEl = document.createElement("div");
    momentEl.style.cssText = "font-size:12px;letter-spacing:.08em;text-transform:uppercase;color:#94A3B8";
    momentEl.textContent = moment;
    const copy = document.createElement("div");
    copy.style.cssText = "flex:1;display:flex;flex-direction:column;justify-content:center;gap:12px";
    const titleEl = document.createElement("div");
    titleEl.style.cssText = "font-size:28px;font-weight:600;line-height:1.2";
    titleEl.textContent = title;
    const bodyEl = document.createElement("div");
    bodyEl.style.cssText = "font-size:16px;line-height:1.5;color:#CBD5E1";
    bodyEl.textContent = body;
    copy.append(titleEl, bodyEl);
    const button = document.createElement("a");
    button.id = "interveen-remember";
    button.href = SAFE_URL;
    button.setAttribute("role", "button");
    button.style.cssText = [
      "display:block", "width:100%", "box-sizing:border-box", "text-align:center",
      "text-decoration:none", "border:0", "border-radius:18px", "padding:16px 22px",
      "background:#10B981", "color:#06281C", "font-size:16px", "font-weight:600",
      "min-height:48px", "line-height:1.2", "pointer-events:auto",
      "touch-action:manipulation", "-webkit-user-select:none",
    ].join(";");
    button.textContent = remember;
    const onRemember = (event) => {
      event.preventDefault();
      rememberGoal(evidence);
    };
    button.addEventListener("click", onRemember);
    button.addEventListener("touchend", onRemember, { passive: false });
    root.append(momentEl, copy, button);
    document.documentElement.style.background = "#0F172A";
    if (document.body) document.body.style.overflow = "hidden";
  }

  function persist() {
    const host = location.hostname || "";
    const mixed = typeof interveenIsMixedHost === "function" && interveenIsMixedHost(host);
    if (!mixed) leaveSite();
    mountOverlay(globalThis.__interveenGoal || {});
  }

  function startGuard() {
    persist();
    document.addEventListener("DOMContentLoaded", persist, true);
    window.addEventListener("load", persist, true);
    try {
      new MutationObserver(() => {
        if (!document.getElementById("interveen-overlay")) persist();
      }).observe(document.documentElement || document, { childList: true, subtree: true });
    } catch (_) {}
    let ticks = 0;
    const timer = setInterval(() => {
      persist();
      if (++ticks >= 40) clearInterval(timer);
    }, 250);

    try {
      api.runtime.sendMessage({ type: "evaluate", url: location.href }).then((response) => {
        if (response && response.configured === true && response.guardEnabled === false) {
          globalThis.__interveenGuardBooted = false;
          document.getElementById("interveen-overlay")?.remove();
          return;
        }
        if (response) {
          globalThis.__interveenGoal = response;
          mountOverlay(response, true);
        }
      }).catch(() => {});
    } catch (_) {}
  }
})();

const api = globalThis.browser || globalThis.chrome;

function isExtensionPage(url) {
  return !url
    || /blocked\.html/i.test(url)
    || url.startsWith("safari-web-extension:")
    || url.startsWith("webkit-extension:")
    || url.startsWith("chrome-extension:");
}

function evaluate(url) {
  if (typeof interveenEvaluate === "function") return interveenEvaluate(url);
  return { matched: /(?:^|[/.])xxx(?:[/?#:]|$)/i.test(url) || /xxx/i.test(String(url)), evidence: url };
}

function blockedUrl() {
  try {
    return api.runtime.getURL("blocked.html");
  } catch (_) {
    return "";
  }
}

function blockTab(tabId, url) {
  if (tabId == null || isExtensionPage(url) || !evaluate(url).matched) return;
  const dest = blockedUrl();
  if (!dest) return;
  api.tabs.update(tabId, { url: dest }).catch(() => {});
}

api.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  const url = changeInfo.url || tab.url;
  if (url) blockTab(tabId, url);
});

if (api.webNavigation) {
  ["onBeforeNavigate", "onCommitted", "onDOMContentLoaded", "onCompleted"].forEach((name) => {
    const event = api.webNavigation[name];
    if (!event || !event.addListener) return;
    event.addListener((details) => {
      if (details.frameId === 0) blockTab(details.tabId, details.url);
    });
  });
}

function leaveTab(tabId) {
  if (tabId == null) return;
  api.tabs.update(tabId, { url: "https://www.google.com/" }).catch(() => {});
}

api.runtime.onMessage.addListener((request, sender, sendResponse) => {
  const tabId = sender && sender.tab && sender.tab.id;
  const url = (request && request.url) || (sender && sender.tab && sender.tab.url) || "";
  if (request && request.type === "evaluate" && tabId != null) {
    blockTab(tabId, url);
  }
  if (request && request.type === "remembered") {
    leaveTab(tabId);
  }
  api.runtime
    .sendNativeMessage("com.interveen.appios", request)
    .then(sendResponse)
    .catch(() => {
      if (request && request.type === "remembered") {
        sendResponse({ ok: true });
        return;
      }
      sendResponse({
        show: true,
        guardEnabled: true,
        configured: false,
        title: "Remember why you started.",
        body: "This pause is the win.",
        moment: "A moment.",
        remember: "I Remember My Goal",
      });
    });
  return true;
});

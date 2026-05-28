const mockResults = [
  { name: "Reporte_Anual_2025.pdf", path: "/Documentos/Reportes/", type: "PDF", size: "2.4 MB" },
  { name: "reporte_summary.docx", path: "/Documentos/Trabajo/", type: "DOCX", size: "840 KB" },
  { name: "reporte_Q1.xlsx", path: "/Finanzas/2025/", type: "XLSX", size: "1.1 MB" },
  { name: "reporte_marketing.pptx", path: "/Presentaciones/", type: "PPTX", size: "5.6 MB" },
  { name: "old_reporte.txt", path: "/Archivos/2024/", type: "TXT", size: "12 KB" }
];

const state = {
  filter: "Todos",
  history: [],
  dirs: ["/home/usuario/Documentos", "/home/usuario/Descargas", "/home/usuario/Proyectos"],
  indexed: 14872,
  queue: 23
};

function setupNavigation() {
  const menuButtons = document.querySelectorAll(".menu-item");
  const pages = document.querySelectorAll(".page");

  menuButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      menuButtons.forEach((b) => b.classList.remove("active"));
      pages.forEach((p) => p.classList.remove("active"));
      btn.classList.add("active");
      document.getElementById(`page-${btn.dataset.page}`).classList.add("active");
    });
  });
}

function renderFilters() {
  const filters = ["Todos", "PDF", "DOCX", "XLSX", "PPTX", "TXT"];
  const box = document.getElementById("search-filters");
  box.innerHTML = "";

  filters.forEach((f) => {
    const b = document.createElement("button");
    b.textContent = f;
    b.className = `filter-btn ${state.filter === f ? "active" : ""}`;
    b.addEventListener("click", () => {
      state.filter = f;
      renderFilters();
      runSearch();
    });
    box.appendChild(b);
  });
}

function renderResults(items) {
  const container = document.getElementById("search-results");
  if (!items.length) {
    container.innerHTML = '<div class="card">Sin resultados.</div>';
    return;
  }

  container.innerHTML = items
    .map(
      (r) => `
      <div class="result-item">
        <strong>${r.name}</strong><br>
        <small>${r.path} • ${r.type} • ${r.size}</small>
      </div>
    `
    )
    .join("");
}

function runSearch() {
  const input = document.getElementById("search-input");
  const q = input.value.trim().toLowerCase();

  if (!q) {
    document.getElementById("search-results").innerHTML = '<p class="subtitle">Escribe algo para buscar.</p>';
    return;
  }

  state.history.unshift(`${q} (${new Date().toLocaleString()})`);
  renderHistory();

  const filtered = mockResults.filter((r) => {
    const byText = r.name.toLowerCase().includes(q);
    const byType = state.filter === "Todos" || r.type === state.filter;
    return byText && byType;
  });

  renderResults(filtered);
}

function renderDirs() {
  const ul = document.getElementById("dir-list");
  ul.innerHTML = state.dirs.map((d) => `<li>${d}</li>`).join("");
}

function setupSettings() {
  const addDirBtn = document.getElementById("add-dir-btn");
  const dirInput = document.getElementById("dir-input");

  addDirBtn.addEventListener("click", () => {
    const value = dirInput.value.trim();
    if (!value) return;
    state.dirs.push(value);
    dirInput.value = "";
    renderDirs();
  });

  renderDirs();
}

function renderHistory() {
  const ul = document.getElementById("history-list");
  if (!state.history.length) {
    ul.innerHTML = "<li>Sin búsquedas aún.</li>";
    return;
  }

  ul.innerHTML = state.history.slice(0, 20).map((h) => `<li>${h}</li>`).join("");
}

function setupHistory() {
  const clearBtn = document.getElementById("clear-history-btn");
  clearBtn.addEventListener("click", () => {
    state.history = [];
    renderHistory();
  });
  renderHistory();
}

function setupMonitor() {
  setInterval(() => {
    const cpu = Math.floor(20 + Math.random() * 60);
    const mem = Math.floor(35 + Math.random() * 50);
    state.indexed += Math.floor(Math.random() * 8);
    state.queue = Math.max(0, state.queue + Math.floor(Math.random() * 7) - 3);

    document.getElementById("kpi-cpu").textContent = `${cpu}%`;
    document.getElementById("kpi-mem").textContent = `${mem}%`;
    document.getElementById("kpi-indexed").textContent = state.indexed.toString();
    document.getElementById("kpi-queue").textContent = state.queue.toString();
  }, 1500);
}

window.addEventListener("DOMContentLoaded", () => {
  document.getElementById("search-btn").addEventListener("click", runSearch);
  document.getElementById("search-input").addEventListener("keydown", (e) => {
    if (e.key === "Enter") runSearch();
  });

  setupNavigation();
  renderFilters();
  setupSettings();
  setupHistory();
  setupMonitor();

  document.getElementById("search-results").innerHTML = '<p class="subtitle">Escribe algo para buscar.</p>';
});

function DashboardToolbar({ onReload, onLogout }) {
  return (
    <section className="toolbar">
      <div>
        <p className="eyebrow">Sesion activa</p>
        <p className="toolbar-text">Token JWT guardado en localStorage</p>
      </div>
      <div className="toolbar-actions">
        <button type="button" className="secondary-button" onClick={onReload}>
          Recargar
        </button>
        <button type="button" className="secondary-button" onClick={onLogout}>
          Salir
        </button>
      </div>
    </section>
  )
}

export default DashboardToolbar

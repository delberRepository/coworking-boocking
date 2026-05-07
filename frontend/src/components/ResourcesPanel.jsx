const ResourcesPanel = ({ resources, dataLoading }) => {
  return (
    <div className="panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Recursos</p>
          <h2>Disponibles para reservar</h2>
        </div>
        {dataLoading ? <span className="muted">Cargando...</span> : null}
      </div>

      <div className="resource-list">
        {resources.map((resource) => (
          <article key={resource.id} className="resource-item">
            <div>
              <h3>{resource.name}</h3>
              <p>
                {resource.type} · capacidad {resource.capacity}
              </p>
            </div>
            <span className={resource.active ? 'pill active' : 'pill inactive'}>
              {resource.active ? 'Activo' : 'Inactivo'}
            </span>
          </article>
        ))}
        {!dataLoading && resources.length === 0 ? (
          <p className="empty-state">No hay recursos visibles para este usuario.</p>
        ) : null}
      </div>
    </div>
  )
}

export default ResourcesPanel

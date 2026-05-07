const BookingFormPanel = ({
  resources,
  bookingForm,
  bookingLoading,
  selectedResource,
  onFieldChange,
  onSubmit,
}) => {
  return (
    <div className="panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Nueva reserva</p>
          <h2>Crear booking</h2>
        </div>
      </div>

      <form className="stack" onSubmit={onSubmit}>
        <label>
          <span>Recurso</span>
          <select
            name="resourceId"
            value={bookingForm.resourceId}
            onChange={onFieldChange}
            required
          >
            <option value="" disabled>
              Selecciona un recurso
            </option>
            {resources
              .filter((resource) => resource.active)
              .map((resource) => (
                <option key={resource.id} value={resource.id}>
                  {resource.name} ({resource.type})
                </option>
              ))}
          </select>
        </label>

        <label>
          <span>Inicio</span>
          <input
            name="start"
            type="datetime-local"
            value={bookingForm.start}
            onChange={onFieldChange}
            required
          />
        </label>

        <label>
          <span>Fin</span>
          <input
            name="end"
            type="datetime-local"
            value={bookingForm.end}
            onChange={onFieldChange}
            required
          />
        </label>

        <button type="submit" className="primary-button" disabled={bookingLoading}>
          {bookingLoading ? 'Guardando...' : 'Reservar'}
        </button>
      </form>

      {selectedResource ? (
        <p className="muted">
          Recurso seleccionado: {selectedResource.name} · {selectedResource.type}
        </p>
      ) : null}
    </div>
  )
}

export default BookingFormPanel

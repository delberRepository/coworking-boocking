import { formatDateTime } from '../lib/date'

function BookingsPanel({ bookings, dataLoading, onCancelBooking }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Mis reservas</p>
          <h2>Bookings del usuario autenticado</h2>
        </div>
      </div>

      <div className="booking-list">
        {bookings.map((booking) => (
          <article key={booking.id} className="booking-item">
            <div>
              <h3>{booking.resourceName}</h3>
              <p>
                {formatDateTime(booking.startTime)} - {formatDateTime(booking.endTime)}
              </p>
              <p className="muted">Estado: {booking.status}</p>
            </div>
            <button
              type="button"
              className="secondary-button"
              onClick={() => onCancelBooking(booking.id)}
            >
              Cancelar
            </button>
          </article>
        ))}
        {!dataLoading && bookings.length === 0 ? (
          <p className="empty-state">Todavia no hay reservas para este usuario.</p>
        ) : null}
      </div>
    </section>
  )
}

export default BookingsPanel

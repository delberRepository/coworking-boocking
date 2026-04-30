import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
const TOKEN_KEY = 'coworking-booking-token'

const emptyAuthForm = {
  email: '',
  password: '',
}

const emptyBookingForm = {
  resourceId: '',
  start: '',
  end: '',
}

async function apiRequest(path, { method = 'GET', token, body } = {}) {
  const headers = {}

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  const contentType = response.headers.get('content-type') ?? ''
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text()

  if (!response.ok) {
    const detail =
      typeof payload === 'string'
        ? payload
        : payload.message || payload.error || JSON.stringify(payload)
    throw new Error(detail || 'La peticion ha fallado')
  }

  return payload
}

function toIsoDateTime(value) {
  return value ? new Date(value).toISOString() : null
}

function formatDateTime(value) {
  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

function App() {
  const [mode, setMode] = useState('login')
  const [authForm, setAuthForm] = useState(emptyAuthForm)
  const [bookingForm, setBookingForm] = useState(emptyBookingForm)
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) ?? '')
  const [resources, setResources] = useState([])
  const [bookings, setBookings] = useState([])
  const [authMessage, setAuthMessage] = useState('')
  const [appMessage, setAppMessage] = useState('')
  const [authLoading, setAuthLoading] = useState(false)
  const [dataLoading, setDataLoading] = useState(false)
  const [bookingLoading, setBookingLoading] = useState(false)

  const selectedResource = useMemo(
    () => resources.find((resource) => String(resource.id) === bookingForm.resourceId),
    [bookingForm.resourceId, resources],
  )

  useEffect(() => {
    if (!token) {
      setResources([])
      setBookings([])
      return
    }

    localStorage.setItem(TOKEN_KEY, token)
    void loadDashboard(token)
  }, [token])

  async function loadDashboard(currentToken = token) {
    setDataLoading(true)
    setAppMessage('')

    try {
      const [resourcesResponse, bookingsResponse] = await Promise.all([
        apiRequest('/resources', { token: currentToken }),
        apiRequest('/bookings/me', { token: currentToken }),
      ])

      setResources(resourcesResponse)
      setBookings(bookingsResponse)
      setBookingForm((current) => ({
        ...current,
        resourceId:
          current.resourceId || (resourcesResponse[0] ? String(resourcesResponse[0].id) : ''),
      }))
    } catch (error) {
      setAppMessage(error.message)
      if (/401|403|Unauthorized|Forbidden/i.test(error.message)) {
        handleLogout()
      }
    } finally {
      setDataLoading(false)
    }
  }

  function handleAuthFieldChange(event) {
    const { name, value } = event.target
    setAuthForm((current) => ({ ...current, [name]: value }))
  }

  function handleBookingFieldChange(event) {
    const { name, value } = event.target
    setBookingForm((current) => ({ ...current, [name]: value }))
  }

  async function handleAuthSubmit(event) {
    event.preventDefault()
    setAuthLoading(true)
    setAuthMessage('')

    try {
      if (mode === 'register') {
        const response = await apiRequest('/auth/register', {
          method: 'POST',
          body: authForm,
        })

        setAuthMessage(typeof response === 'string' ? response : 'Usuario registrado')
        setMode('login')
        return
      }

      const loginToken = await apiRequest('/auth/login', {
        method: 'POST',
        body: authForm,
      })

      setToken(loginToken)
      setAuthMessage('Sesion iniciada')
      setAuthForm(emptyAuthForm)
    } catch (error) {
      setAuthMessage(error.message)
    } finally {
      setAuthLoading(false)
    }
  }

  async function handleCreateBooking(event) {
    event.preventDefault()
    setBookingLoading(true)
    setAppMessage('')

    try {
      await apiRequest('/bookings', {
        method: 'POST',
        token,
        body: {
          resourceId: Number(bookingForm.resourceId),
          start: toIsoDateTime(bookingForm.start),
          end: toIsoDateTime(bookingForm.end),
        },
      })

      setBookingForm((current) => ({
        ...current,
        start: '',
        end: '',
      }))
      await loadDashboard()
      setAppMessage('Reserva creada correctamente')
    } catch (error) {
      setAppMessage(error.message)
    } finally {
      setBookingLoading(false)
    }
  }

  async function handleCancelBooking(bookingId) {
    setAppMessage('')

    try {
      await apiRequest(`/bookings/${bookingId}`, {
        method: 'DELETE',
        token,
      })
      await loadDashboard()
      setAppMessage('Reserva cancelada')
    } catch (error) {
      setAppMessage(error.message)
    }
  }

  function handleLogout() {
    localStorage.removeItem(TOKEN_KEY)
    setToken('')
    setAuthMessage('')
    setAppMessage('')
  }

  return (
    <main className="app-shell">
      <section className="hero-band">
        <div>
          <p className="eyebrow">Coworking Booking</p>
          <h1>Cliente React conectado al API REST de Spring</h1>
          <p className="hero-copy">
            Login con JWT, consulta de recursos y gestion basica de reservas.
          </p>
        </div>
        <div className="api-chip">API: {API_BASE_URL}</div>
      </section>

      {!token ? (
        <section className="panel auth-panel">
          <div className="segmented-control" role="tablist" aria-label="Modo de acceso">
            <button
              type="button"
              className={mode === 'login' ? 'active' : ''}
              onClick={() => setMode('login')}
            >
              Login
            </button>
            <button
              type="button"
              className={mode === 'register' ? 'active' : ''}
              onClick={() => setMode('register')}
            >
              Registro
            </button>
          </div>

          <form className="stack" onSubmit={handleAuthSubmit}>
            <label>
              <span>Email</span>
              <input
                name="email"
                type="email"
                value={authForm.email}
                onChange={handleAuthFieldChange}
                placeholder="usuario@empresa.com"
                required
              />
            </label>

            <label>
              <span>Contrasena</span>
              <input
                name="password"
                type="password"
                value={authForm.password}
                onChange={handleAuthFieldChange}
                placeholder="Minimo 6 caracteres"
                required
              />
            </label>

            <button type="submit" className="primary-button" disabled={authLoading}>
              {authLoading
                ? 'Enviando...'
                : mode === 'login'
                  ? 'Entrar'
                  : 'Crear cuenta'}
            </button>
          </form>

          {authMessage ? <p className="status-line">{authMessage}</p> : null}
        </section>
      ) : (
        <>
          <section className="toolbar">
            <div>
              <p className="eyebrow">Sesion activa</p>
              <p className="toolbar-text">Token JWT guardado en localStorage</p>
            </div>
            <div className="toolbar-actions">
              <button type="button" className="secondary-button" onClick={() => loadDashboard()}>
                Recargar
              </button>
              <button type="button" className="secondary-button" onClick={handleLogout}>
                Salir
              </button>
            </div>
          </section>

          {appMessage ? <p className="status-line">{appMessage}</p> : null}

          <section className="dashboard-grid">
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

            <div className="panel">
              <div className="panel-header">
                <div>
                  <p className="eyebrow">Nueva reserva</p>
                  <h2>Crear booking</h2>
                </div>
              </div>

              <form className="stack" onSubmit={handleCreateBooking}>
                <label>
                  <span>Recurso</span>
                  <select
                    name="resourceId"
                    value={bookingForm.resourceId}
                    onChange={handleBookingFieldChange}
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
                    onChange={handleBookingFieldChange}
                    required
                  />
                </label>

                <label>
                  <span>Fin</span>
                  <input
                    name="end"
                    type="datetime-local"
                    value={bookingForm.end}
                    onChange={handleBookingFieldChange}
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
          </section>

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
                    onClick={() => handleCancelBooking(booking.id)}
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
        </>
      )}
    </main>
  )
}

export default App

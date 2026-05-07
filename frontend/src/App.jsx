import { useEffect, useMemo, useState } from 'react'
import './App.css'
import AuthPanel from './components/AuthPanel'
import BookingFormPanel from './components/BookingFormPanel'
import BookingsPanel from './components/BookingsPanel'
import DashboardToolbar from './components/DashboardToolbar'
import HeroBanner from './components/HeroBanner'
import ResourcesPanel from './components/ResourcesPanel'
import { apiRequest, API_BASE_URL } from './lib/api'
import { toIsoDateTime } from './lib/date'

const TOKEN_KEY = 'coworking-booking-token'
const EMAIL_KEY = 'coworking-booking-email'

const emptyAuthForm = {
  email: '',
  password: '',
}

const emptyBookingForm = {
  resourceId: '',
  start: '',
  end: '',
}

const App = () => {
  const [mode, setMode] = useState('login')
  const [authForm, setAuthForm] = useState(emptyAuthForm)
  const [bookingForm, setBookingForm] = useState(emptyBookingForm)
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) ?? '')
  const [loginEmail, setLoginEmail] = useState(() => localStorage.getItem(EMAIL_KEY) ?? '')
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
      localStorage.removeItem(TOKEN_KEY)
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
      setLoginEmail(authForm.email)
      localStorage.setItem(EMAIL_KEY, authForm.email)
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
    localStorage.removeItem(EMAIL_KEY)
    setToken('')
    setLoginEmail('')
    setAuthMessage('')
    setAppMessage('')
  }

  return (
    <main className="app-shell">
      <HeroBanner
          login={loginEmail}
          isAuthenticated={Boolean(token)}
      />

      {!token ? (
        <AuthPanel
          mode={mode}
          authForm={authForm}
          authLoading={authLoading}
          authMessage={authMessage}
          onModeChange={setMode}
          onFieldChange={handleAuthFieldChange}
          onSubmit={handleAuthSubmit}
        />
      ) : (
        <>
          <DashboardToolbar onReload={() => loadDashboard()} onLogout={handleLogout} />

          {appMessage ? <p className="status-line">{appMessage}</p> : null}

          <section className="dashboard-grid">
            <ResourcesPanel resources={resources} dataLoading={dataLoading} />
            <BookingFormPanel
              resources={resources}
              bookingForm={bookingForm}
              bookingLoading={bookingLoading}
              selectedResource={selectedResource}
              onFieldChange={handleBookingFieldChange}
              onSubmit={handleCreateBooking}
            />
          </section>

          <BookingsPanel
            bookings={bookings}
            dataLoading={dataLoading}
            onCancelBooking={handleCancelBooking}
          />
        </>
      )}
    </main>
  )
}

export default App

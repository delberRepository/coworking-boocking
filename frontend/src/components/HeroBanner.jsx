import logo from '../assets/1.jpg'

function HeroBanner({ apiBaseUrl, isAuthenticated }) {
  return (

    <section className="hero-band">
      <div>
        <p className="eyebrow">Co-working Booking</p>
        <h1>CO-WORKING PRANATURE & VINDALOO ESTUDIOS</h1>
        <h2>Realiza tus reservas</h2>
          {!isAuthenticated && (
            <p className="hero-copy">
              Introduce tus datos para consulta de recursos y gestion de reservas.
            </p>
           )}
      </div>
        <div className="api-chip">
            API: {apiBaseUrl}
            <img src={logo} alt="logo" className="hero-logo"/>
        </div>
    </section>
  )
}

export default HeroBanner

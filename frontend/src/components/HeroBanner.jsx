import logo from '../assets/1.jpg'

function HeroBanner({login,  isAuthenticated }) {
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
        {isAuthenticated && (
        <div className="api-chip">
             {login}
            <img src={logo} alt="logo" className="hero-logo"/>
        </div>
            )}
    </section>
  )
}

export default HeroBanner

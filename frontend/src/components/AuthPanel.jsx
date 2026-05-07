const AuthPanel = ({
  mode,
  authForm,
  authLoading,
  authMessage,
  onModeChange,
  onFieldChange,
  onSubmit,
}) => {
  return (
    <section className="panel auth-panel">
      <div className="segmented-control" role="tablist" aria-label="Modo de acceso">
        <button
          type="button"
          className={mode === 'login' ? 'active' : ''}
          onClick={() => onModeChange('login')}
        >
          Login
        </button>
        <button
          type="button"
          className={mode === 'register' ? 'active' : ''}
          onClick={() => onModeChange('register')}
        >
          Registro
        </button>
      </div>

      <form className="stack" onSubmit={onSubmit}>
        <label>
          <span>Email</span>
          <input
            name="email"
            type="email"
            value={authForm.email}
            onChange={onFieldChange}
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
            onChange={onFieldChange}
            placeholder="Minimo 6 caracteres"
            required
          />
        </label>

        <button type="submit" className="primary-button" disabled={authLoading}>
          {authLoading ? 'Enviando...' : mode === 'login' ? 'Entrar' : 'Crear cuenta'}
        </button>
      </form>

      {authMessage ? <p className="status-line">{authMessage}</p> : null}
    </section>
  )
}

export default AuthPanel

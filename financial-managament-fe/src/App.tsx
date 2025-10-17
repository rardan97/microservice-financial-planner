
import { Route, Routes } from 'react-router-dom'
import './App.css'
import AppLayout from './layouts/AppLayout'
import Home from './pages/Home'
import Category from './pages/Category'
import Login from './pages/Auth/Login'
import Register from './pages/Auth/Register'
import { AuthProvider } from './context/AuthContext'
import PrivateRoute from './utils/PrivateRoute'

function App() {
return (
    <>
    <AuthProvider>
    <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={
        <PrivateRoute>
            <AppLayout />
        </PrivateRoute>
        }>
        <Route index element={<Home />} />
        <Route path="/category" element={<Category />} /></Route>
    </Routes>
    </AuthProvider>
    </>
)
}

export default App

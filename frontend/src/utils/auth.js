export const handleAuthError = async (error) => {
    if (!error) return false;

    // Check for common JWT/Auth expiration messages
    const message = error.message || '';
    if (
        message.includes('JWT expired') ||
        message.includes('jwt expired') ||
        message.includes('invalid claim: missing sub claim') ||
        message.includes('Invalid token')
    ) {
        console.warn('Authentication token expired. Signing out...');
        try {
            localStorage.removeItem('andavar_token');
            localStorage.removeItem('andavar_user');
        } catch (e) {
            console.error('Error during sign out:', e);
        }
        window.location.reload();
        return true;
    }

    return false;
};

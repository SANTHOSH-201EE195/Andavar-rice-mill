import './AboutUs.css';

export default function AboutUs() {
    return (
        <section className="about-us-section">
            <div className="container" style={{ textAlign: 'center', marginBottom: '3rem' }}>
                <div className="section-title-wrapper">
                    <span className="subtitle">OUR PROMISE</span>
                    <h2 className="main-title">Traditional & Pure</h2>
                    <div className="title-decoration"></div>
                </div>
            </div>
            <div className="container about-container">
                {/* Left Column: Image Collage */}
                <div className="about-images">
                    <div className="img-box img-1">
                        <img src="/images/masala_combo.png" alt="Traditional Wood Press" />
                    </div>
                    <div className="img-box img-2">
                        <img src="https://images.pexels.com/photos/12284682/pexels-photo-12284682.jpeg?auto=compress&cs=tinysrgb&w=800" alt="Pure Oil" />
                    </div>
                    <div className="img-box img-3">
                        <img src="/images/oil_combo.png" alt="Spices" />
                    </div>

                    {/* Call Us Badge */}
                    <div className="call-us-badge">
                        <div className="phone-icon-circle">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                            </svg>
                        </div>
                        <div className="call-text">
                            <span className="subtitle">Call Us Anytime</span>
                            <span className="number">+91 7010903976</span>
                        </div>
                    </div>
                </div>

                {/* Right Column: Content */}
                <div className="about-content">
                    <span className="kicker">PREMIUM QUALITY</span>
                    <h2 className="section-title">
                        Sekku Oils &<br />
                        Authentic Masalas
                    </h2>
                    <p className="description">
                        At Andavar, we bring you the goodness of tradition with our sekku oils and hand-ground masalas.
                        Our products are made from the finest ingredients, ensuring 100% purity and natural taste,
                        just like how our ancestors made them.
                    </p>

                    <ul className="feature-list">
                        <li>Traditional Sekku Pressed Method</li>
                        <li>100% Pure, Natural & Unrefined</li>
                        <li>No Chemicals, Preservatives or Adulteration</li>
                        <li>Farm Fresh Ingredients Sourced Directly</li>
                    </ul>

                    <div className="signature-block">
                        <div className="profile">
                            <img src="/images/ceo.png" alt="Thiruganasekaran B" className="ceo-avatar" />
                            <div className="profile-info">
                                <h4>Thiruganasekaran B</h4>
                                <span className="role">Director & CEO</span>
                            </div>
                        </div>

                        <div className="badge-wrapper">
                            <div className="circular-badge">
                                <svg viewBox="0 0 100 100" width="80" height="80">
                                    <path id="circlePath" d="M 50, 50 m -37, 0 a 37,37 0 1,1 74,0 a 37,37 0 1,1 -74,0" fill="transparent" />
                                    <text fill="#ffffff" fontSize="12" fontWeight="bold" letterSpacing="1.2">
                                        <textPath href="#circlePath" startOffset="50%" textAnchor="middle">
                                            ANDAVAR • TRADITIONAL & PURE •
                                        </textPath>
                                    </text>
                                </svg>
                                <div className="badge-icon">🌿</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}

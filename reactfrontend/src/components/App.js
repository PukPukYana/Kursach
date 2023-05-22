import './css/App.css';
import {useEffect, useState} from "react";
import HomePage from "./HomePage";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import ProfilePage from "./ProfilePage";
import {UserProvider} from "../contexts/UserContext";
import LoginPage from "./LoginPage";
import SmLinkPage from "./SmLinkPage";
import {CsrfConsumer, CsrfProvider} from "../contexts/CsrfContext";
import CompleteRegPage from "./CompleteRegPage";
import ItemStackPage from "./ItemStackPage";
import CreateItemStackPage from "./CreateItemStackPage";
import SyncProductsPage from "./SyncProductsPage";
import ArchivePage from "./ArchivePage";

function App() {

    const [userContextValue, setUserContextValue] = useState({
        user: null,
        isLoaded: false,
        hasError: false
    });

    const [csrfContextValue, setCsrfContextValue] = useState({
        csrfToken: null,
        csrfHeaderName: null,
        isLoaded: false
    });

    useEffect(() => {

        const abortController = new AbortController();

        const fetchUser = async ()=> {

            try {
                const response = await fetch('/api/user', {
                   signal: abortController.signal
                });

                if(response.status === 200) {
                    setUserContextValue({user: await response.json(), isLoaded: true, hasError: false});
                } else if (response.status >= 500) {
                    setUserContextValue({user: null, isLoaded: true, hasError: true});
                } else {
                    setUserContextValue({user: null, isLoaded: true, hasError: false});
                }

            } catch (error) {
                if (error.name !== "AbortError") {
                    throw error;
                }
            }
        }

        fetchUser();

        return () => abortController.abort();
    }, []);

    useEffect(() => {

        const abortController = new AbortController();

        const fetchCsrfToken = async () => {

            try {
                const response = await fetch('/api/csrf', {
                    signal: abortController.signal
                });

                if(response.status === 200) {

                   const responseData = await response.json();
                   setCsrfContextValue({
                       csrfToken: responseData.csrfToken,
                       csrfHeaderName: responseData.csrfHeaderName,
                       isLoaded: true
                   });
                } else {
                    setCsrfContextValue({
                        csrfToken: null,
                        csrfHeaderName: null,
                        isLoaded: false
                    });
                }

            } catch (error) {
                if (error.name !== "AbortError") {
                    throw error;
                }
            }
        }
        fetchCsrfToken();

        return () => abortController.abort();
    }, []);

    return (
        <UserProvider value={userContextValue}>
            <CsrfProvider value={csrfContextValue}>
                <BrowserRouter>
                    <Routes>
                        <Route index element={<HomePage />} />
                        <Route path="archive" element={<ArchivePage /> } />
                        <Route path="login" element={<LoginPage /> } />
                        <Route path="smlink" element={<SmLinkPage /> } />
                        <Route path="profile" element={<ProfilePage />} />
                        <Route path="complete" element={<CompleteRegPage />} />
                        <Route path="item-stack/new" element={<CreateItemStackPage />} />
                        <Route path="item-stack/:id" element={<ItemStackPage />} />
                        <Route path="sync" element={<SyncProductsPage />} />
                    </Routes>
                </BrowserRouter>
            </CsrfProvider>
        </UserProvider>
    );

}

export default App;

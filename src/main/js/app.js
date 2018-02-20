const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {
    render() {
        return (
            <ResumeSearch />
        );
    }
}

class ResumeSearch extends React.Component {

    constructor(props) {
        super(props);
        this.state = {resumes: []};
        this.onScroll = this.onScroll.bind(this);
        this.onSearch = this.onSearch.bind(this);
    }

    componentWillMount() {
        window.addEventListener('scroll', this.onScroll, false);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.onScroll, false);
    }

    onSearch(e) {
        e.preventDefault();

        var skills = document.querySelector('#skills').value.split(',')
            .map(a => a.trim()).filter(a => a.length > 0);

        var experience = document.querySelector('#exp').value || 0;
        var sort = document.querySelector('#sort').value;

        this.retrieveResumes({skills, experience, sort}, 0, []);
    }

    retrieveResumes(data, page, resumes) {
        this.setState({pending: true});
        client({
            method: 'POST',
            entity: data,
            path: `/resume/search?page=${page}`,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.setState({
                next: page + 1, data, pending: false,
                elementsReceived: response.entity.length !== 0,
                resumes: [...resumes, ...response.entity]
            });
            this.onScroll();
        }).catch(response => {
            this.setState({pending: false});
            console.log(response.entity);
        });
    }

    onScroll() {
        var scrollTop = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
        var scrollHeight = (document.documentElement && document.documentElement.scrollHeight) || document.body.scrollHeight;
        var clientHeight = document.documentElement.clientHeight || window.innerHeight;

        if (Math.ceil(scrollTop + clientHeight) >= scrollHeight - 100 && this.state.elementsReceived && !this.state.pending) {
            this.retrieveResumes(this.state.data, this.state.next, this.state.resumes);
        }
    }

    render() {
        return (
            <div>
                <div className='input-group'>
                    <div className='input-group-append'>
                        <span className='input-group-text'>Total</span>
                    </div>
                    <input type='number' className='form-control' min='0' placeholder='Experience...' id='exp'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>years of experience with skillSet</span>
                    </div>
                    <input type='text' className='form-control' placeholder='Skills...' id='skills'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>sorted by</span>
                    </div>
                    <select className='custom-select' id='sort'>
                        <option value='relevance'>Relevance</option>
                        <option value='experience'>Experience</option>
                    </select>
                    <div className='input-group-append'>
                        <button className='btn btn-outline-secondary' type='button' onClick={this.onSearch}>Search...</button>
                    </div>
                </div>
                <ResumeList resumes={this.state.resumes}/>
                <Popup />
            </div>
        );
    }
}

class ResumeList extends React.Component {
    onClick(e) {
        e.preventDefault();

        if (e.target.classList.contains('open-file')) {
            window.open(`/resume/open?id=${e.target.id}`);
        }
    }

    render() {
        var resumes = this.props.resumes.map((resume, index) =>
            <Resume key={resume.id} index={index} resume={resume}/>
        );
        return (
            <div className='table-responsive'>
                <table className='table table-hover'>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Resume</th>
                            <th>Email</th>
                            <th>Experience</th>
                            <th>Notes</th>
                        </tr>
                    </thead>
                    <tbody onClick={this.onClick}>
                        {resumes}
                    </tbody>
                </table>
            </div>
        );
    }
}

class Resume extends React.Component {
    constructor(props) {
        super(props);
        this.openPopup = this.openPopup.bind(this);
    }

    openPopup(e) {
        e.preventDefault();
        document.dispatchEvent(new CustomEvent('open-popup', { detail: this.props.resume }));
    }

    render() {
        return (
            <tr>
                <td>{this.props.index + 1}</td>
                <td><a href='#' id={this.props.resume.id} className='card-link open-file'>{this.props.resume.fileName}</a></td>
                <td>{this.props.resume.email}</td>
                <td>{this.props.resume.experience}</td>
                <td><button className='btn btn-light' type='button' onClick={this.openPopup}>Notes...</button></td>
            </tr>
        )
    }
}

class Popup extends React.Component {
    constructor(props) {
        super(props);

        this.save = this.save.bind(this);
        this.cancel = this.cancel.bind(this);
        this.openPopup = this.openPopup.bind(this);
    }

    componentDidMount() {
        document.addEventListener('open-popup', this.openPopup);
    }

    componentWillUnmount() {
        document.removeEventListener('open-popup', this.openPopup);
    }

    openPopup(e) {
        e.preventDefault();

        document.querySelector('#backdrop').classList.toggle('hide');

        this.resume = e.detail;
        var popup = document.querySelector('#popup');

        popup.classList.toggle('show');
        popup.querySelector('#noteTitle').innerHTML = this.resume.fileName;
        popup.querySelector('#noteArea').value = this.resume.notes;
    }

    save(e) {
        e.preventDefault();

        var notes = document.querySelector('#popup #noteArea').value;

        client({
            method: 'POST',
            entity: notes,
            path: `/resume/notes?id=${this.resume.id}`,
            headers: {'Content-Type': 'text/plain'}
        }).then(() => {
            this.resume.notes = notes;
            this.resume = null;
        }).catch(response => {
            console.log(response.entity);
        });

        this.closePopup();
    }

    cancel(e) {
        e.preventDefault();
        this.closePopup();
    }

    closePopup() {
        document.querySelector('#backdrop').classList.toggle('hide');
        var popup = document.querySelector('#popup');

        popup.classList.toggle('show');
        popup.querySelector('#noteTitle').innerHTML = '';
        popup.querySelector('#noteArea').value = '';
    }

    render() {
        return (
            <div>
                <div id='popup' className='modal'>
                    <div className='modal-dialog modal-lg modal-dialog-centered'>
                        <div className='modal-content'>
                            <div className='modal-header'>
                                <h5 className='modal-title' id='noteTitle'></h5>
                                <button type='button' className='close' onClick={this.cancel}>
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className='modal-body'>
                                <textarea id='noteArea' className='form-control'></textarea>
                            </div>
                            <div className='modal-footer'>
                                <button type='button' className='btn btn-secondary' onClick={this.cancel}>Close</button>
                                <button type='button' className='btn btn-primary' onClick={this.save}>Save</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div id='backdrop' className='dropdown-backdrop hide'></div>
            </div>
        );
    }
}

ReactDOM.render(
    <App />,
    document.querySelector('#container')
)

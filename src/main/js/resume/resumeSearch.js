const React = require('react');
const ResumeList = require('./resumeList');
const client = require('./../client');

class ResumeSearch extends React.Component {

    constructor(props) {
        super(props);
        this.state = {resumes: []};
        this.details = {};
        this.onScroll = this.onScroll.bind(this);
        this.onSearch = this.onSearch.bind(this);
        this.resumeUpdate = this.resumeUpdate.bind(this);
    }

    componentWillMount() {
        document.addEventListener('scroll', this.onScroll, false);
        document.addEventListener('resume-update', this.resumeUpdate, false);
    }

    componentWillUnmount() {
        document.removeEventListener('scroll', this.onScroll, false);
        document.removeEventListener('resume-update', this.resumeUpdate, false);
    }

    resumeUpdate() {
        this.setState(this.state);
    }

    onSearch(e) {
        e.preventDefault();

        var skills = document.querySelector('#skills').value.split(',')
            .map(a => a.trim()).filter(a => a.length > 0);

        var experience = document.querySelector('#exp').value || 0;
        var sort = document.querySelector('#sort').value;

        this.retrieveResumes({skills, experience, sort});
    }

    retrieveResumes(data, page = 0, resumes = []) {
        this.details.pending = true;
        client({
            method: 'POST',
            entity: data,
            path: `/resume/search?page=${page}`,
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.setState({resumes: [...resumes, ...response.entity]});
            this.details = {
                next: page + 1, data, pending: false,
                count: response.entity.length
            };

            document.dispatchEvent(new Event('scroll'));
        }).catch(response => {
            this.details.pending = false;
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {type: 'danger', text: 'Search failed'}}));
        });
    }

    onScroll() {
        var scrollTop = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
        var scrollHeight = (document.documentElement && document.documentElement.scrollHeight) || document.body.scrollHeight;
        var clientHeight = document.documentElement.clientHeight || window.innerHeight;

        if (Math.ceil(scrollTop + clientHeight) >= scrollHeight - 100 && this.details.count && !this.details.pending) {
            this.retrieveResumes(this.details.data, this.details.next, this.state.resumes);
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
                        <button className='btn btn-light' type='button' onClick={this.onSearch}>Search...</button>
                    </div>
                </div>
                <ResumeList resumes={this.state.resumes}/>
            </div>
        );
    }
}

module.exports = ResumeSearch;
